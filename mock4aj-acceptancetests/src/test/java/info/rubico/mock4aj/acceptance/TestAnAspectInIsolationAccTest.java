package info.rubico.mock4aj.acceptance;

import static info.rubico.mock4aj.Mock4AspectJ.createWeavedProxy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import info.rubico.mock4aj.acceptance.testdata.MarkTargetAtAspect;
import info.rubico.mock4aj.acceptance.testdata.SecondMarkTargetAtAspect;
import info.rubico.mock4aj.acceptance.testdata.Target;

import org.junit.Test;

/**
 * <h1>Test an aspect in isolation</h1>
 * 
 * <h2>Story</h2>
 * <p>
 * As a unit tester, I must be able to test an aspect in isolation to test this aspect without side
 * effects (def. of unit test).
 * 
 * In order to do that, I must be able to activate only the aspect under test (AUT) and control when
 * and which classes will be weaved.
 * </p>
 */
public class TestAnAspectInIsolationAccTest {

    @Test
    public void canChooseWhichAspectToWeave() {
        Target targetMocked = mock(Target.class);
        Class<?> registeredAspect = MarkTargetAtAspect.class;
        Class<?> notRegisteredAspect = SecondMarkTargetAtAspect.class;

        Target proxy = createWeavedProxy(targetMocked, registeredAspect);
        proxy.theMethod();

        verify(targetMocked).markWeaved(registeredAspect);
        verify(targetMocked, never()).markWeaved(notRegisteredAspect);
    }

    @Test
    public void canWeaveMultipleAspects() {
        Target targetMocked = mock(Target.class);
        Class<?> firstAspect = MarkTargetAtAspect.class;
        Class<?> secondAspect = SecondMarkTargetAtAspect.class;

        Target proxy = createWeavedProxy(targetMocked, firstAspect, secondAspect);
        proxy.theMethod();

        verify(targetMocked).markWeaved(firstAspect);
        verify(targetMocked).markWeaved(secondAspect);
    }

    @Test
    public void canChooseWhichObjectsToWeave() {
        Class<?> aspect = MarkTargetAtAspect.class;
        Target mockToWeave = mock(Target.class);
        Target mockToNotWeave = mock(Target.class);

        Target proxy = createWeavedProxy(mockToWeave, aspect);
        proxy.theMethod();

        verify(mockToWeave).markWeaved(aspect);
        verify(mockToNotWeave, never()).markWeaved(aspect);
    }

    @Test
    public void canWeaveMultipleObjectsIndependently() {
        Class<?> aspect = MarkTargetAtAspect.class;
        Target firstMock = mock(Target.class);
        Target secondMock = mock(Target.class);

        Target firstProxy = createWeavedProxy(firstMock, aspect);
        firstProxy.theMethod();
        Target secondProxy = createWeavedProxy(secondMock, aspect);
        secondProxy.theMethod();

        verify(firstMock).markWeaved(aspect);
        verify(secondMock).markWeaved(aspect);
    }

}
