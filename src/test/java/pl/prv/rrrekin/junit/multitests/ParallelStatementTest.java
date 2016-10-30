package pl.prv.rrrekin.junit.multitests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

/**
 * @author Michal Rudewicz <michal.rudewicz@ericsson.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class ParallelStatementTest {
    private static final int TEST_COUNT = 42;
    private static final long TEST_TIMEOUT = 100;

    private MultiTestsRule.ParallelStatement statement;

    @Mock private Statement origStatement;

    @Before
    public void setUp() throws Exception {
        statement = new MultiTestsRule.ParallelStatement(TEST_COUNT, TEST_TIMEOUT, origStatement);
    }

    @Test
    public void repeatConfiguredNumberOfTimes() throws Throwable {
        // when
        statement.evaluate();
        // then
        then(origStatement).should(times(TEST_COUNT)).evaluate();
    }

    @Test(expected = Exception.class)
    public void shouldFailAfterFirstException() throws Throwable {
        //given
        doNothing().doNothing().doThrow(new Exception()).when(origStatement).evaluate();
        // when
        statement.evaluate();
        // then
        then(origStatement).should(times(3)).evaluate();
    }

    @Test(expected = AssertionError.class)
    public void shouldFailAfterFirstAssertion() throws Throwable {
        //given
        doNothing().doNothing().doThrow(new AssertionError()).when(origStatement).evaluate();
        // when
        statement.evaluate();
        // then
        then(origStatement).should(times(3)).evaluate();
    }

    @Test
    public void shouldFailOnTimeout() throws Throwable {
        //given
        doAnswer(new Answer() {
            @Override
            public Void answer(final InvocationOnMock invocation) {
                try {
                    Thread.sleep(TEST_TIMEOUT * 2);
                } catch (final InterruptedException ignored) {
                }
                return null;
            }
        }).when(origStatement).evaluate();
        // when
        try {
            statement.evaluate();
            fail("Unexpected success");
        } catch (final TimeoutException ignored) {
        }
        // then
        then(origStatement).should(times(TEST_COUNT)).evaluate();
    }
}