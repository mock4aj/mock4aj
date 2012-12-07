package info.rubico.mock4aj.codegen.cglib.calls.caller;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import info.rubico.mock4aj.api.calls.CallSource;
import info.rubico.mock4aj.api.calls.CallTarget;
import info.rubico.mock4aj.api.calls.MethodCaller;
import info.rubico.mock4aj.api.exceptions.EncapsulatedExceptionThrownByTarget;
import info.rubico.mock4aj.api.exceptions.Mock4AjException;
import info.rubico.mock4aj.api.exceptions.UnsupportedSourceType;
import info.rubico.mock4aj.internal.calls.CallTargetByReflection;

import java.util.Date;

import net.sf.cglib.asm.ClassVisitor;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unused")
public class MethodCallerBytecodeGeneratorTest { // NOPMD

    private static final String SET_TIME = "setTime";
    private static final String GET_TIME = "getTime";
    private static final Object[] NO_ARGS = {};

    private Date dateMock;
    private CallTarget getTimeTarget;
    private CallTarget setTimeTarget;

    @Before
    public void setupGeneratorToTargetDateGetTime() throws NoSuchMethodException {
        dateMock = mock(Date.class);

        getTimeTarget = new CallTargetByReflection(dateMock, GET_TIME);
        setTimeTarget = new CallTargetByReflection(dateMock, SET_TIME, long.class);
    }

    @Test
    public void whenCreateThenCreateAWeavedCaller() {
        Object caller = createGenerator(getTimeTarget).create();
        assertThat(caller, instanceOf(MethodCaller.class));
    }

    @Test(expected = Mock4AjException.class)
    public void givenAnExceptionDuringTheGenerationThenBoxItIntoAMock4AjException() {
        MethodCallerBytecodeGenerator generatorWithError = generatorWithGenerationError();
        generatorWithError.create();
    }

    @Test(expected = Mock4AjException.class)
    public void givenAnExceptionDuringTheInstantiationThenShouldBoxItIntoAMock4AjException() {
        MethodCallerBytecodeGenerator generatorWithError = generatorWithInstantiationError();
        generatorWithError.create();
    }

    @Test
    public void givenAnMock4AjExceptionDuringCreationThenNotBoxedIntoAnOtherException() {
        MethodCallerBytecodeGenerator generatorWithError = generatorWithInstantiationError();
        try {
            generatorWithError.create();
        }
        catch (Mock4AjException error) {
            assertThat(error.getCause(), not(is(Mock4AjException.class)));
        }
    }

    @Test
    public void whenCreateThenCallerClassNameShouldHaveTheNameOfTheTargetInIt()
        throws NoSuchMethodException {

        CallTargetByReflection target = new CallTargetByReflection(new Date(), GET_TIME);
        Object caller = createGenerator(target).create();

        String targetShortClassName = "Date";
        String targetMethodeName = GET_TIME;
        String expectedName = String.format("$$CallerByMock4Aj$$%s$%s$$",
                                            targetShortClassName, targetMethodeName);

        assertThat(caller.getClass().getName(), containsString(expectedName));
    }

    @Test
    public void givenMockTargetWhenCreateThenTheCallerClassNameShouldKeepItshort() {
        Object caller = createGenerator(getTimeTarget).create();

        String targetShortClassName = "Date";
        String targetMethodeName = GET_TIME;
        String expectedName = String.format("$$CallerByMock4Aj$$%s_Enhanced$%s$$",
                                            targetShortClassName, targetMethodeName);

        assertThat(caller.getClass().getName(), containsString(expectedName));
    }

    @Test
    public void canSetNamingPolicy() {
        MethodCallerBytecodeGenerator generator = new MethodCallerBytecodeGenerator(getTimeTarget);

        final String name = "CHANGED";
        generator.setNamingPolicy(new NamingPolicy() {
            public String getClassName(String prefix, String source, Object key, Predicate names) {
                return name;
            }
        });
        MethodCaller caller = generator.create();

        assertEquals(name, caller.getClass().getSimpleName());
    }

    @Test
    public void byDefaultWhenCreateThenTheSourceShouldBeMethodCaller() {
        Class<?> methodCallerClass = MethodCaller.class;
        Object caller = createGenerator(getTimeTarget).create();
        assertThat(caller, instanceOf(methodCallerClass));
        assertClassPrefixName(methodCallerClass.getName(), caller);
    }

    @Test
    public void givenASourceClassWhenCreateThenTheCallerShouldFakeSameTypeAndName() {
        String sourceName = "X";
        Class<?> sourceType = Date.class;
        CallSource source = mockCallSource(sourceName, sourceType);

        Object caller = createGenerator(getTimeTarget, source).create();

        assertClassPrefixName(source.getName(), caller);
        assertThat("Should extends the class source type",
                   caller, instanceOf(source.getType()));
    }

    @Test
    public void givenASourceInterfaceWhenCreateThenTheCallerShouldFakeNameAndImplementsIt() {
        String sourceName = "X";
        Class<?> sourceInterface = ASourceInterface.class;
        CallSource source = mockCallSource(sourceName, sourceInterface);

        Object caller = createGenerator(getTimeTarget, source).create();

        assertClassPrefixName(source.getName(), caller);
        assertThat("Should implements the interface source type",
                   caller, instanceOf(source.getType()));
    }

    @Test
    public void givenASourceClassNameWhenCreateThenTheCallerShouldBeginWithThisName() {
        String name = "SomeName";
        CallSource source = mock(CallSource.class);
        doReturn(name).when(source).getName();
        doReturn(Object.class).when(source).getType();

        Object caller = createGenerator(getTimeTarget, source).create();

        assertClassPrefixName(name, caller);
    }

    @Test(expected = UnsupportedSourceType.class)
    public void canNotUseFinalClassesAsASource() {
        String sourceName = "X";
        Class<?> sourceFinalClass = String.class;
        CallSource source = mockCallSource(sourceName, sourceFinalClass);

        Object caller = createGenerator(getTimeTarget, source).create();
    }

    @Test
    public void canTargetPrimitives() throws NoSuchMethodException {
        final int integer = 5;
        CallTargetByReflection primitiveTarget = new CallTargetByReflection(integer, "doubleValue");

        MethodCaller caller = createGenerator(primitiveTarget).create();
        caller.doCall(NO_ARGS);
    }

    @Test
    public void whenDoCallThenShouldCallTheTarget() {
        MethodCaller caller = createGenerator(getTimeTarget).create();
        caller.doCall(NO_ARGS);
        verify(dateMock).getTime();
    }

    @Test
    public void whenDoCallThenTheReturnValueShouldFollow() {
        final long time = 10L;
        given(dateMock.getTime()).willReturn(time);

        MethodCaller caller = createGenerator(getTimeTarget).create();
        long result = (Long) caller.doCall(NO_ARGS);

        assertEquals(time, result);
    }

    @Test
    public void givenVoidTargetMethodWhenDoCallThenReturnNull() {
        final CallTarget voidTarget = setTimeTarget;
        MethodCaller caller = createGenerator(voidTarget).create();

        final long time = 45L;
        Object result = caller.doCall(new Object[] { time });

        assertNull("Result from a void target should be null.", result);
    }

    @Test
    public void givenArgumentsWhenDoCallThenShouldCallTheTargetWithThem()
        throws NoSuchMethodException {

        ClassForParams mock = mock(ClassForParams.class);
        CallTargetByReflection argsTarget = new CallTargetByReflection(mock,
                                                                       "returnItsParams",
                                                                       Object.class, Object.class);

        MethodCaller caller = createGenerator(argsTarget).create();
        final Date arg1 = new Date();
        final int arg2 = 5;
        caller.doCall(new Object[] { arg1, arg2 });

        verify(mock).returnItsParams(arg1, arg2);
    }

    @Test
    public void givenUnboxArgumentWhenDoCallThenShouldCallTheTargetCorrectly() {
        final CallTarget targetWithUnboxedArg = setTimeTarget;
        MethodCaller caller = createGenerator(targetWithUnboxedArg).create();

        final long time = 45L;
        caller.doCall(new Object[] { time });

        verify(dateMock).setTime(time);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenWrongNumberOfArgumentsWhenDoCallThenIllegalArgumentException() {
        final CallTarget targetWithAnArg = setTimeTarget;
        MethodCaller caller = createGenerator(targetWithAnArg).create();

        caller.doCall(NO_ARGS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenWrongArgumentTypeWhenDoCallThenIllegalArgumentException() {
        final CallTarget targetWithALongArg = setTimeTarget;
        MethodCaller caller = createGenerator(targetWithALongArg).create();

        caller.doCall(new Object[] { "NOT_A_LONG" });
    }

    @Test
    public void givenWrongArgumentTypeWhenDoCallThenShouldBoxTheCastExceptionAsTheCause() {
        final CallTarget targetWithALongArg = setTimeTarget;
        MethodCaller caller = createGenerator(targetWithALongArg).create();

        try {
            caller.doCall(new Object[] { "NOT_A_LONG" });
        }
        catch (IllegalArgumentException error) {
            assertThat("The ClassCastException should be boxed as the cause.",
                       error.getCause(), instanceOf(ClassCastException.class));
        }
    }

    @Test
    public void givenTargetThrowingExceptionWhenDoCallThenShouldRethrow()
        throws NoSuchMethodException {

        ClassWithThrowingMethod throwingObject = new ClassWithThrowingMethod();
        CallTargetByReflection throwingTarget = new CallTargetByReflection(throwingObject,
                                                                           "throwingMethod");
        MethodCaller caller = createGenerator(throwingTarget).create();

        try {
            caller.doCall(NO_ARGS);
            fail("Exceptions throw by the target should be boxed into an ExceptionFromCallTarget.");
        }
        catch (EncapsulatedExceptionThrownByTarget exceptionBox) {
            assertThat(exceptionBox.getCause(), instanceOf(AnException.class));
        }
    }

    private CallSource mockCallSource(String sourceName, Class<?> sourceType) {
        CallSource source = mock(CallSource.class);
        doReturn(sourceName).when(source).getName();
        doReturn(sourceType).when(source).getType();
        return source;
    }

    private MethodCallerBytecodeGenerator createGenerator(CallTarget target, CallSource callSource) {
        MethodCallerBytecodeGenerator generator = createGenerator(target);
        generator.setSource(callSource);
        return generator;
    }

    private MethodCallerBytecodeGenerator createGenerator(CallTarget target) {
        MethodCallerBytecodeGenerator generator = new MethodCallerBytecodeGenerator(target);
        generator.setUseCache(false); // Tests must be independent
        return generator;
    }

    private MethodCallerBytecodeGenerator generatorWithGenerationError() {
        MethodCallerBytecodeGenerator generator = new MethodCallerBytecodeGenerator(getTimeTarget) {
            @Override
            public void generateClass(ClassVisitor visitor) {
                throw new RuntimeException("TEST"); // NOPMD
            }
        };
        generator.setUseCache(false);
        return generator;
    }

    private MethodCallerBytecodeGenerator generatorWithInstantiationError() {
        MethodCallerBytecodeGenerator generator = new MethodCallerBytecodeGenerator(getTimeTarget) {
            @SuppressWarnings("rawtypes")
            @Override
            protected Object firstInstance(Class type) {
                throw new Mock4AjException("TEST"); // NOPMD
            }
        };
        generator.setUseCache(false);
        return generator;
    }

    private void assertClassPrefixName(String expected, Object objectToVerify) {
        String str = String.format("%s$$", expected);
        String actualName = objectToVerify.getClass().getName();
        assertThat(actualName, containsString(str));
        assertTrue("Must begin with the name of the class.", actualName.startsWith(str));
    }

    @SuppressWarnings("serial")
    public static class AnException extends Exception {

    }

    public static class ClassWithThrowingMethod {
        public static final AnException THE_EXCEPTION = new AnException();

        public void throwingMethod() throws AnException {
            throw THE_EXCEPTION;
        }
    }

    public static class ASourceClass {

    }

    public interface ASourceInterface {

    }

    public static class ClassForParams {
        public Object[] returnItsParams(final Object arg1, final Object arg2) {
            return new Object[] { arg1, arg2 };
        }
    }
}
