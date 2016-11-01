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

package com.github.rrrekin.junit.multitests;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * Indicates that the test should be executed multiple times (default 10) in parallel, all executions have to succeed in
 * given timeout, default 10000ms. Used by the MultiTestsRule.
 *
 * @author Michal Rudewicz <michal.rudewicz@gmail.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD})
public @interface Parallel {

    int DEFAULT_THREAD_COUNT = 10;
    int DEFAULT_TIMEOUT_MS = 10000;

    /** Number of parallel execution threads. */
    int value() default DEFAULT_THREAD_COUNT;

    /** Thread execution timeout in milliseconds. */
    long timeout() default DEFAULT_TIMEOUT_MS;
}
