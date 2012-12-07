package info.rubico.mock4aj.acceptance;

import static info.rubico.mock4aj.Mock4AspectJ.createWeavedProxy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import info.rubico.mock4aj.acceptance.testdata.MarkTargetAtAspect;
import info.rubico.mock4aj.acceptance.testdata.Target;

import org.junit.Test;

/**
 * <h1>Verify the aspect behaviour on real method execution</h1>
 * 
 * <p>
 * As a unit tester, I want to test if existing methods execution in my classes will be correctly
 * matched or not by the aspect under test and if the aspect's behavior (executed advice) is what
 * expected, so thay I can see if a pointcut target what it should.
 * </p>
 * <h2>Acceptance criteria</h2>
 * <ul>
 * <li>the execution of a method that should be matched by a pointcut triggers the correct behavior
 * (the aspects' effect);</li>
 * <li>the execution of a method that should not be matched by a pointcut doesn't trigger any advice
 * (the aspect has no effect/behavior).</li>
 * </ul>
 */
public class VerifyBehaviourOnRealMethodExecutionAccTest {

    @Test
    public void canTestWhenMatchedMethodExecutionThenExpectedAdviceBehaviour() {
        Target mock = mock(Target.class);

        Target proxy = createWeavedProxy(mock, MarkTargetAtAspect.class);
        proxy.theMethod(); // Matched

        verify(mock).markWeaved(any(Class.class));
    }

    @Test
    public void canTestWhenNotMatchedMethodExecutionThenNoEffect() {
        Target mock = mock(Target.class);

        Target proxy = createWeavedProxy(mock, MarkTargetAtAspect.class);
        proxy.otherMethod(); // Not matched

        verify(mock, never()).markWeaved(any(Class.class));
    }
}
