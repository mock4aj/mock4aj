package info.rubico.mock4aj.acceptance;

import static info.rubico.mock4aj.Mock4AspectJ.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import info.rubico.mock4aj.acceptance.testdata.AbstractAspect;
import info.rubico.mock4aj.acceptance.testdata.OtherClass;
import info.rubico.mock4aj.acceptance.testdata.SomeException;
import info.rubico.mock4aj.acceptance.testdata.Target;
import info.rubico.mock4aj.api.calls.CallContext;
import info.rubico.mock4aj.api.exceptions.NotAConcreteAspect;
import info.rubico.mock4aj.api.exceptions.NotAnAspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.junit.Before;
import org.junit.Test;

/**
 * <h1>Simulate calls to a target that should be matched by aspects</h1>
 * 
 * <h2>Story</h2>
 * <p>
 * As a developer, I want to be able to test <strong>"call" primitives</strong> so that I can test
 * if a call pointcut matches what is should.
 * </p>
 * 
 * <h2>Conversation</h2>
 * <p>
 * In order to do that, I need to be able to call the targeted method. However, <strong>the
 * problem</strong> is that the "call" primitive matches the joint point <strong>from which the call
 * is originating (the source) and not the target</strong>.
 * </p>
 * <h2>Conversation</h2>
 * <p>
 * Intuitively, the call to the targeted method would occurs from the test. However it is
 * <strong>not possible since the test class is not weaved and can't be weaved at runtime with only
 * selected aspects</strong>. So we need to offer a flexible solution to simulate calls coming from
 * a weaved class. Since we want to be able to select the aspect to be weaved at runtime within the
 * test, we need to generated dynamically the caller class and weave it.
 * </p>
 * <h2>Related stories</h2>
 * <p>
 * This story offers only a simple simulator that allows to simulate simple calls without
 * restrictions on the source of the call. For a more advanced context simulation, see the story
 * {@link SimulateCallsComingFromAnExistingClassAccTest}.
 * </p>
 * 
 */
public class SimulateCallsToATargetAccTest { // NOPMD

    // CHECKSTYLE:OFF

    @Aspect
    @SuppressAjWarnings("adviceDidNotMatch")
    public static class AspectUnderTest {

        @Around("call(int *..Target.retInt()) && !within(*Test)  && target(target)")
        public int modifyReturnAndMarkOnCallToRetInt(Target target, ProceedingJoinPoint pjp)
                throws Throwable {
            target.markWeaved(getClass());

            int normalValue = (Integer) pjp.proceed();
            return RETINT_ADDED_VALUE + normalValue;
        }

        @After("call(void *..Target.theMethod()) && target(target) && !within(*Test)")
        public void markOnCallToTheMethod(Target target) {
            target.markWeaved(getClass());
        }
    }

    @Aspect
    @SuppressAjWarnings("adviceDidNotMatch")
    public static class OtherAspect {

        @After("call(void *..Target.theMethod()) && target(target) && !within(*Test)")
        public void markOnCallToTheMethod(Target target) {
            target.markWeaved(getClass());
        }
    }

    @Aspect
    @SuppressAjWarnings("adviceDidNotMatch")
    public static class ThrowingAspect {

        @org.aspectj.lang.annotation.Around("call(void *..Target.theMethod()) && !within(*Test)")
        public void throwExceptionOnTheMethod() {
            throw new RuntimeException(); // NOPMD
        }

        @org.aspectj.lang.annotation.After("call(void *..Target.throwingMethod()) && !within(*Test)")
        public void throwExceptionOnThrowingMethod() throws SomeException {
            throw new SomeException();
        }
    }

    // CHECKSTYLE:ON

    private static final int RETINT_ADDED_VALUE = 50;
    private static final int RETINT_NORMAL_VALUE = 10;
    private Target targetMock;

    @Before
    public void setupMockOfTarget() {
        targetMock = mock(Target.class);
        when(targetMock.retInt()).thenReturn(RETINT_NORMAL_VALUE);
    }

    @Test
    public void canNotCallDirectlyFromTheTest() {
        targetMock.retInt(); // Will call Target.retInt()

        verify(targetMock).retInt(); // Is called...
        verify(targetMock, never()).markWeaved(AspectUnderTest.class); // ... but didn't match
    }

    @Test
    public void canTestAMatchingCallPointcut() {
        CallContext context = callContext();
        context.withAspect(AspectUnderTest.class);

        call(targetMock, context).retInt();

        verify(targetMock).markWeaved(AspectUnderTest.class); // Advice was executed
        verify(targetMock).retInt(); // After PC so should execute the original method.
    }

    @Test
    public void canTestANonMatchingCallPointcut() {
        CallContext context = callContext();
        context.withAspect(AspectUnderTest.class);

        call(targetMock, context).otherMethod(); // Should not be weaved

        verify(targetMock).otherMethod(); // Method was executed
        verify(targetMock, never()).markWeaved(AspectUnderTest.class); // Advice was not executed
    }

    @Test
    public void adviceChangingReturnValueShouldBeApplied() {
        CallContext context = callContext();
        context.withAspect(AspectUnderTest.class);

        int retValue = call(targetMock, context).retInt();

        assertEquals(RETINT_ADDED_VALUE + RETINT_NORMAL_VALUE, retValue);
    }

    @Test(expected = SomeException.class)
    public void adviceThrowingSomeExceptionShouldBeApplied() throws SomeException {
        doNothing().when(targetMock).throwingMethod(); // Normally won't throw
        CallContext context = callContext();
        context.withAspect(ThrowingAspect.class);

        call(targetMock, context).throwingMethod(); // Aspect will throw something
    }

    @Test(expected = RuntimeException.class)
    public void adviceThrowingRuntimeExceptionShouldBeApplied() {
        CallContext context = callContext();
        context.withAspect(ThrowingAspect.class);

        call(targetMock, context).theMethod();
    }

    @Test(expected = NotAnAspect.class)
    public void canNotWeaveNonAspect() {
        CallContext context = callContext();

        context.withAspect(OtherClass.class);
        call(targetMock, context).theMethod();
    }

    @Test(expected = NotAConcreteAspect.class)
    public void canNotWeaveAbstractAspect() {
        CallContext context = callContext();

        context.withAspect(AbstractAspect.class);
        call(targetMock, context).theMethod();
    }

    @Test
    public void canHaveIndependantContexts() {
        CallContext contextWithAUT = callContext();
        contextWithAUT.withAspect(AspectUnderTest.class);
        call(targetMock, contextWithAUT).theMethod();

        CallContext contextWithOtherAspect = callContext();
        contextWithOtherAspect.withAspect(OtherAspect.class);
        call(targetMock, contextWithOtherAspect).theMethod();

        verify(targetMock).markWeaved(AspectUnderTest.class);
        verify(targetMock).markWeaved(OtherAspect.class);
        verify(targetMock, times(2)).markWeaved((Class<?>) anyObject()); // No other call to mark
    }

    @Test
    public void canReuseSameContext() {
        Target firstTarget = mock(Target.class);
        Target secondTarget = mock(Target.class);
        CallContext context = callContext();
        context.withAspect(AspectUnderTest.class);

        call(firstTarget, context).retInt();
        call(secondTarget, context).theMethod();

        verify(firstTarget).markWeaved(AspectUnderTest.class);
        verify(secondTarget).markWeaved(AspectUnderTest.class);
    }

    @Test
    public void canWeaveMultipleAspects() {
        CallContext context = callContext();
        context.withAspect(AspectUnderTest.class);
        context.withAspect(OtherAspect.class);

        call(targetMock, context).theMethod();

        verify(targetMock).markWeaved(AspectUnderTest.class);
        verify(targetMock).markWeaved(OtherAspect.class);
    }

    @Test
    public void canTargetMultipleMethodsOnTheSameObject() {
        CallContext context = callContext();
        context.withAspect(AspectUnderTest.class);

        call(targetMock, context).retInt();
        call(targetMock, context).theMethod();

        verify(targetMock, times(2)).markWeaved(AspectUnderTest.class);
    }

    @Test
    public void canTargetSameMethodTwice() {
        CallContext context = callContext();
        context.withAspect(AspectUnderTest.class);

        call(targetMock, context).theMethod();
        call(targetMock, context).theMethod();

        verify(targetMock, times(2)).markWeaved(AspectUnderTest.class);
    }
}
