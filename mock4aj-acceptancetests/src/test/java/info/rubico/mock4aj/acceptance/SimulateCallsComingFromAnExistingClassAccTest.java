package info.rubico.mock4aj.acceptance;

import static info.rubico.mock4aj.Mock4AspectJ.*;
import static org.mockito.Mockito.*;
import info.rubico.mock4aj.acceptance.testdata.Target;
import info.rubico.mock4aj.api.calls.CallContext;
import info.rubico.mock4aj.testdata.ASource;
import info.rubico.mock4aj.testdata.ParentInterfaceSource;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.junit.Before;
import org.junit.Test;

/**
 * <h1>Simulate calls originating from an existing source class</h1>
 * 
 * <h2>Story</h2>
 * <p>
 * As a developer, I want to be able to simulate a class originating from a given existing class so
 * that, I can test "call" primitives with restrictions on the source name (within) and type (this).
 * 
 * </p>
 * <h2>Related stories</h2>
 * <p>
 * This story adds the possibility to define a source (from) context to the call simulator. See the
 * story {@link SimulateCallsToATargetAccTest} for a basic and simple usage of the simulator.
 * </p>
 * <p>
 * It is also possible to simulate a call from a fictitious (non existing) class. See the story
 * {@link SimulateCallsComingFromAFictitiousClassAccTest}.
 * </p>
 */
public class SimulateCallsComingFromAnExistingClassAccTest { // NOPMD

    // CHECKSTYLE:OFF

    @Aspect
    @SuppressAjWarnings("adviceDidNotMatch")
    public static abstract class MarkCallsToRetIntNotFromTest {

        @Pointcut
        public abstract void callFromPointcut();

        @After("call(int *..Target.retInt()) && target(target) && !within(*AccTest) "
                + "&& callFromPointcut()")
        public void markTargetOnRetInt(Target target) {
            target.markWeaved(getClass());
        }
    }

    @Aspect
    public static class HasSourceInName extends MarkCallsToRetIntNotFromTest {

        @Pointcut("(within(*..*Source*) || within(*Source*))")
        public void callFromPointcut() {
        }

    }

    @Aspect
    public static class ImplementsParentInterfaceSource extends MarkCallsToRetIntNotFromTest {

        @Pointcut("this(info.rubico.mock4aj.testdata.ParentInterfaceSource)")
        public void callFromPointcut() {
        }
    }

    @Aspect
    public static class IsStrictlyASourceType extends MarkCallsToRetIntNotFromTest {

        @Pointcut("this(info.rubico.mock4aj.testdata.ASource)")
        public void callFromPointcut() {
        }

    }

    // CHECKSTYLE:ON

    private Target targetMock;
    private CallContext contextWithAllAspects;

    @Before
    public void setupMockOfTarget() {
        contextWithAllAspects = callContext();
        contextWithAllAspects.withAspect(HasSourceInName.class);
        contextWithAllAspects.withAspect(ImplementsParentInterfaceSource.class);
        contextWithAllAspects.withAspect(IsStrictlyASourceType.class);

        targetMock = mock(Target.class);
    }

    @Test
    public void wontWorkWithoutACallSimulator() {
        call(targetMock, contextWithAllAspects).retInt();

        verify(targetMock, never()).markWeaved(HasSourceInName.class);
        verify(targetMock, never()).markWeaved(ImplementsParentInterfaceSource.class);
        verify(targetMock, never()).markWeaved(IsStrictlyASourceType.class);
    }

    @Test
    public void canSimulateACallFromAnExistingClass() {
        contextWithAllAspects.from(ASource.class);

        call(targetMock, contextWithAllAspects).retInt();

        verify(targetMock).markWeaved(HasSourceInName.class);
        verify(targetMock).markWeaved(ImplementsParentInterfaceSource.class);
        verify(targetMock).markWeaved(IsStrictlyASourceType.class);
    }

    @Test
    public void canSimulateACallFromAnExistingInterface() {
        contextWithAllAspects.from(ParentInterfaceSource.class);

        call(targetMock, contextWithAllAspects).retInt();

        verify(targetMock).markWeaved(HasSourceInName.class);
        verify(targetMock).markWeaved(ImplementsParentInterfaceSource.class);
        verify(targetMock, never()).markWeaved(IsStrictlyASourceType.class);
    }

}
