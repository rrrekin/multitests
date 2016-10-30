package pl.prv.rrrekin.junit.multitests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

/**
 * @author Michal Rudewicz <michal.rudewicz@ericsson.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class RepeatStatementTest {

    private static final int TEST_COUNT = 42;

    private MultiTestsRule.RepeatStatement statement;

    @Mock private Statement origStatement;

    @Before
    public void setUp() throws Exception {
        statement = new MultiTestsRule.RepeatStatement(TEST_COUNT, origStatement);
    }

    @Test
    public void repeatConfiguredNumberOfTimes() throws Throwable {
        // when
        statement.evaluate();
        // then
        then(origStatement).should(times(TEST_COUNT)).evaluate();
    }

    @Test
    public void shouldFailAfterFirstException() throws Throwable {
        //given
        doNothing().doNothing().doThrow(new Exception()).when(origStatement).evaluate();
        // when
        try {
            statement.evaluate();
            fail("Unexpected success");
        } catch (final Exception ignored) {
        }
        // then
        then(origStatement).should(times(3)).evaluate();
    }

    @Test
    public void shouldFailAfterFirstAssertion() throws Throwable {
        //given
        doNothing().doNothing().doThrow(new AssertionError()).when(origStatement).evaluate();
        // when
        try {
            statement.evaluate();
            fail("Unexpected success");
        } catch (final AssertionError ignored) {
        }
        // then
        then(origStatement).should(times(3)).evaluate();
    }
}