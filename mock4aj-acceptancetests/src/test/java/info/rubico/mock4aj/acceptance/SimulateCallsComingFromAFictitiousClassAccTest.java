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
 * <h1>Simulate calls originating from an fictitious (non-existing) source class</h1>
 * 
 * <h2>Story</h2>
 * <p>
 * As a developer, I want to be able to test "call" primitives with simulated calls coming from
 * hypothetical (fictitious) classes because I want to test if poincuts target only what it should.
 * </p>
 */
public class SimulateCallsComingFromAFictitiousClassAccTest { // NOPMD

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
    public void canSimulateFromClassNameInDefaultPackage() {
        String matchingName = "MySourceX";

        contextWithAllAspects.from(fakeSourceClass(matchingName));
        call(targetMock, contextWithAllAspects).retInt();

        verify(targetMock).markWeaved(HasSourceInName.class);
        verify(targetMock, never()).markWeaved(ImplementsParentInterfaceSource.class);
        verify(targetMock, never()).markWeaved(IsStrictlyASourceType.class);
    }

    @Test
    public void canSimulateFromClassNameInAGivenPackage() {
        String matchingName = "info.rubico.tests.MySourceX";

        contextWithAllAspects.from(fakeSourceClass(matchingName));
        call(targetMock, contextWithAllAspects).retInt();

        verify(targetMock).markWeaved(HasSourceInName.class);
        verify(targetMock, never()).markWeaved(ImplementsParentInterfaceSource.class);
        verify(targetMock, never()).markWeaved(IsStrictlyASourceType.class);
    }

    @Test
    public void canSimulateFromSomeClassNameAndExtendingAType() {
        String matchingName = "MySourceX";
        Class<?> matchingType = ASource.class;

        contextWithAllAspects.from(fakeSourceClass(matchingName).extending(matchingType));
        call(targetMock, contextWithAllAspects).retInt();

        verify(targetMock).markWeaved(HasSourceInName.class);
        verify(targetMock).markWeaved(ImplementsParentInterfaceSource.class);
        verify(targetMock).markWeaved(IsStrictlyASourceType.class);
    }

    @Test
    public void canSimulateFromSomeClassNameAndImplementingAnInterface() {
        String matchingName = "MySourceX";
        Class<?> matchingType = ParentInterfaceSource.class;

        contextWithAllAspects.from(fakeSourceClass(matchingName).implementing(matchingType));
        call(targetMock, contextWithAllAspects).retInt();

        verify(targetMock).markWeaved(HasSourceInName.class);
        verify(targetMock).markWeaved(ImplementsParentInterfaceSource.class);
    }

}
