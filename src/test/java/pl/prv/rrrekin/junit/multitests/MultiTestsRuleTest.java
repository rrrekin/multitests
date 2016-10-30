package pl.prv.rrrekin.junit.multitests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * @author Michal Rudewicz <michal.rudewicz@ericsson.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class MultiTestsRuleTest {

    private static final int TEST_COUNT = 3;
    private static final int TEST_PAR_COUNT = 5;
    private static final long TEST_TIMEOUT = 100;
    @Mock private Statement statement;
    @Mock private Description description;
    @Mock private Retry retryAnnotation;
    @Mock private Repeat repeatAnnotation;
    @Mock private Parallel parallelAnnotation;

    private MultiTestsRule rule;

    @Before
    public void setUp() throws Throwable {
        rule = new MultiTestsRule();
        doThrow(Exception.class).doThrow(Exception.class).doNothing().when(statement).evaluate();
        when(retryAnnotation.value()).thenReturn(TEST_COUNT);
        when(repeatAnnotation.value()).thenReturn(TEST_COUNT);
        when(parallelAnnotation.value()).thenReturn(TEST_PAR_COUNT);
        when(parallelAnnotation.timeout()).thenReturn(TEST_TIMEOUT);
    }

    @Test
    public void shouldDoNothingWithoutAnnotations() throws Exception {
        // given
        when(description.getAnnotation(Retry.class)).thenReturn(null);
        when(description.getAnnotation(Repeat.class)).thenReturn(null);
        when(description.getAnnotation(Parallel.class)).thenReturn(null);
        // when
        final Statement result = rule.apply(statement, description);
        // then
        assertSame("Returned statement", statement, result);
    }

    @Test
    public void shouldWrapWithRetryStatement() throws Throwable {
        // given

        when(description.getAnnotation(Retry.class)).thenReturn(retryAnnotation);
        when(description.getAnnotation(Repeat.class)).thenReturn(null);
        when(description.getAnnotation(Parallel.class)).thenReturn(null);
        // when
        final Statement result = rule.apply(statement, description);
        // then
        assertThat("Not wrapped with RetryStatement", result, instanceOf(MultiTestsRule.RetryStatement.class));

        // when
        result.evaluate();
        // then single retry
        then(statement).should(times(3)).evaluate();
    }

    @Test
    public void shouldWrapWithRepeatStatement() throws Throwable {
        // given
        doNothing().when(statement).evaluate(); // no retry, so cannot fail
        when(description.getAnnotation(Retry.class)).thenReturn(null);
        when(description.getAnnotation(Repeat.class)).thenReturn(repeatAnnotation);
        when(description.getAnnotation(Parallel.class)).thenReturn(null);
        // when
        final Statement result = rule.apply(statement, description);
        // then
        assertThat("Not wrapped with RepeatStatement", result, instanceOf(MultiTestsRule.RepeatStatement.class));

        // when
        result.evaluate();
        // then
        then(statement).should(times(TEST_COUNT)).evaluate();
    }

    @Test
    public void shouldWrapWithParallelStatement() throws Throwable {
        // given
        doNothing().when(statement).evaluate(); // no retry, so cannot fail
        when(description.getAnnotation(Retry.class)).thenReturn(null);
        when(description.getAnnotation(Repeat.class)).thenReturn(null);
        when(description.getAnnotation(Parallel.class)).thenReturn(parallelAnnotation);
        // when
        final Statement result = rule.apply(statement, description);
        // then
        assertThat("Not wrapped with ParallelStatement", result, instanceOf(MultiTestsRule.ParallelStatement.class));

        // when
        result.evaluate();
        // then
        then(statement).should(times(TEST_PAR_COUNT)).evaluate();
    }

    @Test
    public void shouldWrapWithRepeatAndRetryStatement() throws Throwable {
        // Every execution retried and all repeated
        // given
        when(description.getAnnotation(Retry.class)).thenReturn(retryAnnotation);
        when(description.getAnnotation(Repeat.class)).thenReturn(repeatAnnotation);
        when(description.getAnnotation(Parallel.class)).thenReturn(null);
        // when
        final Statement result = rule.apply(statement, description);
        // then
        assertThat("Not wrapped with RepeatStatement", result, instanceOf(MultiTestsRule.RepeatStatement.class));

        // when
        result.evaluate();
        // then
        then(statement).should(times(TEST_COUNT + 2)).evaluate();
    }

    @Test
    public void shouldWrapWithParallelAndRetryStatement() throws Throwable {
        // Every execution retried and all started in parallel
        // given
        when(description.getAnnotation(Retry.class)).thenReturn(retryAnnotation);
        when(description.getAnnotation(Repeat.class)).thenReturn(null);
        when(description.getAnnotation(Parallel.class)).thenReturn(parallelAnnotation);
        // when
        final Statement result = rule.apply(statement, description);
        // then
        assertThat("Not wrapped with ParallelStatement", result, instanceOf(MultiTestsRule.ParallelStatement.class));

        // when
        result.evaluate();
        // then
        then(statement).should(times(TEST_PAR_COUNT + 2)).evaluate();
    }

    @Test
    public void shouldWrapWithParallelAndRepeatStatement() throws Throwable {
        // Every execution retried and all started in parallel
        // given
        doNothing().when(statement).evaluate(); // no retry, so cannot fail
        when(description.getAnnotation(Retry.class)).thenReturn(null);
        when(description.getAnnotation(Repeat.class)).thenReturn(repeatAnnotation);
        when(description.getAnnotation(Parallel.class)).thenReturn(parallelAnnotation);
        // when
        final Statement result = rule.apply(statement, description);
        // then
        assertThat("Not wrapped with ParallelStatement", result, instanceOf(MultiTestsRule.ParallelStatement.class));

        // when
        result.evaluate();
        // then
        then(statement).should(times(TEST_COUNT * TEST_PAR_COUNT)).evaluate();
    }

    @Test
    public void shouldWrapWithAllStatements() throws Throwable {
        // Every execution retried and all repeated and started in parallel
        // given
        when(description.getAnnotation(Retry.class)).thenReturn(retryAnnotation);
        when(description.getAnnotation(Repeat.class)).thenReturn(repeatAnnotation);
        when(description.getAnnotation(Parallel.class)).thenReturn(parallelAnnotation);
        // when
        final Statement result = rule.apply(statement, description);
        // then
        assertThat("Not wrapped with ParallelStatement", result, instanceOf(MultiTestsRule.ParallelStatement.class));

        // when
        result.evaluate();
        // then
        then(statement).should(times(TEST_COUNT * TEST_PAR_COUNT + 2)).evaluate();
    }

}