package pl.prv.rrrekin.junit.multitests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

/**
 * @author Michal Rudewicz <michal.rudewicz@ericsson.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class RetryStatementTest {

    private static final int TEST_COUNT = 42;

    private MultiTestsRule.RetryStatement statement;

    @Mock private Statement origStatement;

    @Before
    public void setUp() throws Exception {
        statement = new MultiTestsRule.RetryStatement(TEST_COUNT, origStatement);
    }

    @Test
    public void executeOnceOnSuccess() throws Throwable {
        // when
        statement.evaluate();
        // then
        then(origStatement).should(times(1)).evaluate();
    }

    @Test
    public void repeatUntilSuccess() throws Throwable {
        //given
        doThrow(AssertionError.class).doThrow(AssertionError.class)
                .doThrow(Exception.class).doThrow(RuntimeException.class)
                .doNothing().when(origStatement).evaluate();
        // when
        statement.evaluate();
        // then
        then(origStatement).should(times(5)).evaluate();
    }

    @Test
    public void shouldFailWhenAlwaysFails() throws Throwable {
        //given
        doThrow(Exception.class).when(origStatement).evaluate();
        // when
        try {
            statement.evaluate();
            fail("Unexpected success");
        } catch (final Exception ignored) {
        }
        // then
        then(origStatement).should(times(TEST_COUNT)).evaluate();
    }
}