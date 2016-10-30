/*
 * Apache License, Version 2.0
 *
 * You may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package pl.prv.rrrekin.junit.multitests;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * JUnit 4 test rule that enables multiple test executions. Using provided annotations: @{@link Retry}, @{@link Repeat}
 * and @{@link Parallel}, tests can be executed respectively:
 *
 * <ul>
 * <li> until success or maximum execution number executions reached with @{@link Retry} annotation </li>
 * <li> specified number of times sequentially with @{@link Repeat} annotation </li>
 * <li> specified number of times in parallel using @{@link Parallel} annotation - test threads are started
 * synchronously using cyclic barrier</li>
 * </ul>
 *
 * @author Michal Rudewicz <michal.rudewicz@gmail.com>
 */
public class MultiTestsRule implements TestRule {

    @Override
    public Statement apply(final Statement statement, final Description description) {
        Statement result = statement;
        final Retry retry = description.getAnnotation(Retry.class);
        if (retry != null) {
            final int times = retry.value();
            result = new RetryStatement(times, result);
        }
        final Repeat repeat = description.getAnnotation(Repeat.class);
        if (repeat != null) {
            final int times = repeat.value();
            result = new RepeatStatement(times, result);
        }
        final Parallel parallel = description.getAnnotation(Parallel.class);
        if (parallel != null) {
            final int times = parallel.value();
            final long timeout = parallel.timeout();
            result = new ParallelStatement(times, timeout, result);
        }

        return result;
    }

    /** Wrapping statement that executes inner statement until success or retry limit reached. */
    static class RetryStatement extends Statement {
        private final int times;
        private final Statement statement;

        RetryStatement(final int times, final Statement statement) {
            this.times = times;
            this.statement = statement;
        }

        @Override
        public void evaluate() throws Throwable {
            Throwable lastError = null;
            for (int i = 0; i < times; i++) {
                try {
                    statement.evaluate();
                    return;
                } catch (final Throwable ex) {
                    lastError = ex;
                }
            }
            if (lastError != null) {
                throw lastError;
            }
        }
    }

    /** Wrapping statement that executes inner statement several times. */
    static class RepeatStatement extends Statement {
        private final int times;
        private final Statement statement;

        RepeatStatement(final int times, final Statement statement) {
            this.times = times;
            this.statement = statement;
        }

        @Override
        public void evaluate() throws Throwable {
            for (int i = 0; i < times; i++) {
                statement.evaluate();
            }
        }
    }

    /** Wrapping statement that executes inner statement several times in parallel. */
    static class ParallelStatement extends Statement {
        private final int times;
        private final long timeout;
        private final Statement statement;
        private final CyclicBarrier startBarrier;
        private final ExecutorService executor;

        ParallelStatement(final int times, final long timeout, final Statement statement) {
            this.times = times;
            this.timeout = timeout;
            this.statement = statement;
            this.startBarrier = new CyclicBarrier(times);
            this.executor = Executors.newFixedThreadPool(times);
        }

        @Override
        public void evaluate() throws Throwable {
            final List<Future<Throwable>> results = new ArrayList<Future<Throwable>>(times);
            for (int i = 0; i < times; i++) {
                results.add(executor.submit(new Callable<Throwable>() {
                    @Override
                    public Throwable call() throws Exception {
                        try {
                            startBarrier.await();
                            statement.evaluate();
                            return null;
                        } catch (final Throwable t) {
                            return t;
                        }
                    }
                }));
            }
            executor.shutdown();
            if (!executor.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
                throw new TimeoutException("Not all parallel tests finished in given timeout.");
            }
            for (final Future<Throwable> result : results) {
                if (result.get() != null) {
                    throw result.get();
                }
            }
        }
    }
}
