package info.rubico.mock4aj.internal.calls;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class CallTargetByReflectionTest { // NOPMD

    private static final ATarget A_TARGET = new ATarget();
    private static final String ATARGET_METHOD_NAME = "method";
    private static final String ATARGET_PRIVATE_METHOD_NAME = "privateMethod";

    private static final Object A_DATE = new Date();
    private static final String GET_TIME_NAME = "getTime";
    private CallTargetByReflection targetDateGetTime;

    @Before
    public void createCglibCallTargetForDateGetTime() throws NoSuchMethodException {
        targetDateGetTime = new CallTargetByReflection(A_DATE, GET_TIME_NAME);
    }

    @Test
    public void shouldReturnAccurateInstanceAndMethodName() {
        assertEquals(A_DATE, targetDateGetTime.getTargetInstance());
        assertEquals(GET_TIME_NAME, targetDateGetTime.getTargetMethodName());
    }

    @Test
    public void shouldDeduceTheClassFromTheInstanceType() {
        assertEquals(Date.class, targetDateGetTime.getTargetClass());
    }

    @Test(expected = NoSuchMethodException.class)
    public void givenNullInstanceThenThrowsNoSuchMethodException() throws NoSuchMethodException {
        new CallTargetByReflection(null, GET_TIME_NAME);
    }

    @Test(expected = NoSuchMethodException.class)
    public void givenNonExistingMethodThenThrowsNoSuchMethodException()
        throws NoSuchMethodException {

        new CallTargetByReflection(A_DATE, "NON_EXISTING");
    }

    @Test(expected = NoSuchMethodException.class)
    public void givenEmptyMethodNameThenThrowsNoSuchMethodException()
        throws NoSuchMethodException {

        new CallTargetByReflection(A_DATE, "");
    }

    @Test
    public void shouldConsiderArgumentsToFindTheRightMethod()
        throws NoSuchMethodException {

        CallTargetByReflection targetVoid = new CallTargetByReflection(A_TARGET,
                                                                       ATARGET_METHOD_NAME);
        CallTargetByReflection targetArg = new CallTargetByReflection(A_TARGET,
                                                                      ATARGET_METHOD_NAME,
                                                                      Object.class);

        assertEquals(0, targetVoid.getTargetMethod().getParameterTypes().length);
        assertEquals(1, targetArg.getTargetMethod().getParameterTypes().length);
        assertArrayEquals(new Object[] { Object.class }, targetArg.getArgumentTypes());
    }

    @Test(expected = NoSuchMethodException.class)
    public void givenWrongNumberOfArgumentsThenThrowsNoSuchMethodExeception()
        throws NoSuchMethodException {

        new CallTargetByReflection(A_TARGET, ATARGET_METHOD_NAME, Object.class, Object.class);
    }

    @Test(expected = NoSuchMethodException.class)
    public void givenWrongArgumentTypesThenThrowsNoSuchMethodException()
        throws NoSuchMethodException {

        new CallTargetByReflection(A_TARGET, ATARGET_METHOD_NAME, String.class);
    }

    @Test(expected = NoSuchMethodException.class)
    public void givenNonPublicTargetThenNoSuchMethod() throws NoSuchMethodException {
        new CallTargetByReflection(A_TARGET, ATARGET_PRIVATE_METHOD_NAME);
    }

    @SuppressWarnings("unused")
    private static class ATarget {
        public void method() { // NOPMD
        }

        public void method(Object arg) { // NOPMD
        }

        private void privateMethod() { // NOPMD
        }
    }
}
