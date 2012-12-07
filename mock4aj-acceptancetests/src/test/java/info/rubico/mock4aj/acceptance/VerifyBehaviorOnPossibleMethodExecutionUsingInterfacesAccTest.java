package info.rubico.mock4aj.acceptance;

import static info.rubico.mock4aj.Mock4AspectJ.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import info.rubico.mock4aj.acceptance.testdata.Target;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.junit.Before;
import org.junit.Test;

/**
 * <h1>Verify the aspect behavior on possible method execution (using interfaces)</h1>
 * <p>
 * As a unit tester, I want to test if potential methods execution will be correctly matched or not
 * by the aspect under test and if the aspect's behavior (executed advice) is what expected.
 * </p>
 * <h2>Acceptance criteria</h2>
 * <ul>
 * <li>the execution of a method that should be matched by a pointcut triggers the correct behavior
 * (the aspects' effect);</li>
 * <li>the execution of a method that should not be matched by a pointcut doesn't trigger any advice
 * (the aspect has no effect/behavior).</li>
 * </ul>
 * <h2>Motivations</h2>
 * <p>
 * We often use wildcards in pointcuts. We want to be able to test if new or renamed methods will be
 * correctly matched.
 * </p>
 * <p>
 * To achieve that goal, we will create some kind of <strong>honey pots</strong> with some methods
 * with names that should be matched and some others that should not. Then, we will verify that only
 * the correct set of methods as been matched and that the correct expected behaviour has been
 * triggered.
 * </p>
 * <h2>Limitations</h2>
 * <p>
 * Using this technique, all "fakes" (simulated) methods are added by creating a class (or directly
 * a mock) that extends or implementing an existing class. So, if the pointcut target specifically
 * an interface or a class (without type+ and/or not using within), those new methods won't be
 * matched.
 * </p>
 */
public class VerifyBehaviorOnPossibleMethodExecutionUsingInterfacesAccTest { // NOPMD

    // CHECKSTYLE:OFF

    @Aspect
    @SuppressAjWarnings("adviceDidNotMatch")
    public static class AspectUnderTest {

        public static int exactTargetTimes = 0;
        public static int withinTargetTimes = 0;
        public static int thisTargetTimes = 0;
        public static int targetPlusTimes = 0;
        public static int someDoTimes = 0;

        public static void reset() {
            exactTargetTimes = 0;
            withinTargetTimes = 0;
            thisTargetTimes = 0;
            targetPlusTimes = 0;
            someDoTimes = 0;
        }

        @org.aspectj.lang.annotation.After("execution(* *..Target.do*(int))")
        public void exactTarget() {
            exactTargetTimes++;
        }

        @org.aspectj.lang.annotation.After("execution(* *..Target+.do*(int))")
        public void targetPlus() {
            targetPlusTimes++;
        }

        @org.aspectj.lang.annotation.After("execution(* do*(int)) && this(Target)")
        public void thisTarget() {
            thisTargetTimes++;
        }

        @org.aspectj.lang.annotation.After("execution(* do*(int)) && within(*..Target)")
        public void withinTarget() {
            withinTargetTimes++;
        }

        @org.aspectj.lang.annotation.After("execution(* do*(int))")
        public void exactSomeDo() {
            someDoTimes++;
        }
    }

    // CHECKSTYLE:ON

    @Before
    public void resetAspect() {
        AspectUnderTest.reset();
    }

    // @formatter:on
    // CHECKSTYLE:ON

    // CHECKSTYLE:OFF
    // @formatter:off

    public interface Honeypot {
        void doX(int x); //NOPMD
        void doXNoArgs();
        void otherName();
    }
    
    // @formatter:on
    // CHECKSTYLE:ON

    @Test
    public void usingAMockOfHoneyPot() {
        Honeypot targetMockedPlusHoneypot = mock(Honeypot.class);
        Honeypot proxy = createWeavedProxy(targetMockedPlusHoneypot, AspectUnderTest.class);

        proxy.doX(0);
        proxy.doXNoArgs();
        proxy.otherName();

        assertEquals(1, AspectUnderTest.someDoTimes);
        assertEquals(0, AspectUnderTest.targetPlusTimes);
        assertEquals(0, AspectUnderTest.withinTargetTimes);
        assertEquals(0, AspectUnderTest.exactTargetTimes);
        assertEquals(0, AspectUnderTest.thisTargetTimes);
    }

    @Test
    public void usingAMockCombiningTarget() {
        Target targetMockedPlusHoneypot = mock(Target.class,
                                               withSettings().extraInterfaces(Honeypot.class));
        Honeypot proxy = (Honeypot) createWeavedProxy(targetMockedPlusHoneypot,
                                                      AspectUnderTest.class);

        proxy.doX(0);
        proxy.doXNoArgs();
        proxy.otherName();

        assertEquals(1, AspectUnderTest.someDoTimes);
        assertEquals(1, AspectUnderTest.targetPlusTimes);
        assertEquals(1, AspectUnderTest.withinTargetTimes);
        assertEquals(1, AspectUnderTest.thisTargetTimes);
        assertEquals(0, AspectUnderTest.exactTargetTimes);
    }

    // CHECKSTYLE:OFF
    // @formatter:off

    public interface ExtendedHoneypot extends Target {
        void doX(int x); //NOPMD
        void doXNoArgs();
        void otherName();
    }
    
    // @formatter:on
    // CHECKSTYLE:ON

    @Test
    public void usingAMockOfAnInheritedInterface() {
        ExtendedHoneypot targetMockedPlusHoneypot = mock(ExtendedHoneypot.class);
        ExtendedHoneypot proxy = createWeavedProxy(targetMockedPlusHoneypot, AspectUnderTest.class);

        proxy.doX(0);
        proxy.doXNoArgs();
        proxy.otherName();

        assertEquals(1, AspectUnderTest.someDoTimes);
        assertEquals(1, AspectUnderTest.targetPlusTimes);
        assertEquals(1, AspectUnderTest.thisTargetTimes);
        assertEquals(0, AspectUnderTest.withinTargetTimes);
        assertEquals(0, AspectUnderTest.exactTargetTimes);
    }
}
