package info.rubico.mock4aj.codegen.cglib.calls.selector;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import info.rubico.mock4aj.api.calls.CallContext;
import info.rubico.mock4aj.api.calls.CallTarget;
import info.rubico.mock4aj.api.calls.MethodCaller;
import info.rubico.mock4aj.api.exceptions.EncapsulatedExceptionThrownByTarget;
import info.rubico.mock4aj.api.exceptions.Mock4AjException;
import info.rubico.mock4aj.internal.calls.CallerGenerator;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

import net.sf.cglib.core.ReflectUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class CglibSimulateCallInterceptorTest { // NOPMD

    private static final Object[] NO_ARGS = new Object[] {};
    private static final Method GET_TIME_METHOD = ReflectUtils
        .findMethod(Date.class.getName() + ".getTime()");
    private static final Method SET_TIME_METHOD = ReflectUtils
        .findMethod(Date.class.getName() + ".setTime(long)");

    private Date targetObjectMock;
    private MethodCaller callerMock;
    private CallerGenerator callerFactoryMock;
    private Object clientOfTheCallbackMock;
    private CallContext callContextMock;

    private CglibSimulateCallInterceptor callback;

    @Before
    public void setupAWeavedCallSimulator() {
        targetObjectMock = mock(Date.class);
        clientOfTheCallbackMock = mock(Object.class);

        callContextMock = mock(CallContext.class);

        callerMock = mock(MethodCaller.class);
        callerFactoryMock = mock(CallerGenerator.class);
        when(callerFactoryMock.generateMethodCaller(anyTarget(), anyContext()))
            .thenReturn(callerMock);

        callback = new CglibSimulateCallInterceptor(targetObjectMock,
                                                    callContextMock,
                                                    callerFactoryMock);
    }

    @Test
    public void whenAMethodIsCalledThenTheRealMethodIsNotInvoked() throws Throwable {
        simulateCall(targetObjectMock, GET_TIME_METHOD);
        verify(targetObjectMock, never()).getTime();
    }

    @Test
    public void whenAMethodIsCalledThenACallerIsGeneratedForTheTargetedMethod() throws Throwable {
        simulateCall(targetObjectMock, GET_TIME_METHOD);

        ArgumentCaptor<CallTarget> callTarget = ArgumentCaptor.forClass(CallTarget.class);
        verify(callerFactoryMock).generateMethodCaller(callTarget.capture(), anyContext());
        assertThat((Date) callTarget.getValue().getTargetInstance(), is(targetObjectMock));
        assertEquals(GET_TIME_METHOD.getName(),
                     callTarget.getValue().getTargetMethodName());
    }

    @Test
    public void whenAMethodIsCalledThenACallerIsGeneratedUseingTheContext() throws Throwable {
        simulateCall(targetObjectMock, GET_TIME_METHOD);

        ArgumentCaptor<CallContext> context;
        context = ArgumentCaptor.forClass(CallContext.class);
        verify(callerFactoryMock).generateMethodCaller(anyTarget(), context.capture());
        assertSame(callContextMock, context.getValue());
    }

    @Test
    public void whenAMethodIsCalledthenTheCallIsMadeOnTheGeneratedCaller() throws Throwable {
        simulateCall(targetObjectMock, GET_TIME_METHOD);
        verify(callerMock).doCall(NO_ARGS);
    }

    @Test
    public void whenAMethodIsCalledThenTheCallerReturnValueFollows() throws Throwable {
        final long time = 5L;
        when(callerMock.doCall(NO_ARGS)).thenReturn(time);

        long ret = (Long) simulateCall(targetObjectMock, GET_TIME_METHOD);

        assertEquals(time, ret);
    }

    @Test(expected = IOException.class)
    public void givenAnExceptionFromTheTargetWhenCallThenTheExceptionIsRethrowUnboxed() throws Throwable {
        Exception error = new IOException();
        doThrow(new EncapsulatedExceptionThrownByTarget(error)).when(callerMock).doCall(NO_ARGS);
        simulateCall(targetObjectMock, GET_TIME_METHOD);
    }

    @Test(expected = Mock4AjException.class)
    public void givenASystemCallerExceptionWhenCallThenExceptionIsBoxedIntoAnMock4AjException() throws Throwable {
        Exception error = new ClassCastException();
        doThrow(new RuntimeException(error)).when(callerMock).doCall(NO_ARGS); // NOPMD
        simulateCall(targetObjectMock, GET_TIME_METHOD);
    }

    @Test
    public void givenAnAjExceptionWhenCallThenExceptionIsNotBoxedTwice() throws Throwable {
        doThrow(new Mock4AjException("aa")).when(callerMock).doCall(NO_ARGS);
        try {
            simulateCall(targetObjectMock, GET_TIME_METHOD);
            fail();
        }
        catch (Mock4AjException exception) {
            assertNull("Mock4AjException should not be boxed twice.", exception.getCause());
        }
    }

    @Test
    public void givenMethodWithArgumentsWhenCallThenArgumentsArePassedToTheCaller() throws Throwable {
        final long time = 5L;
        simulateCall(targetObjectMock, SET_TIME_METHOD, time);
        verify(callerMock).doCall(new Object[] { time });
    }

    private Object simulateCall(Object target, Method method, Object... args) throws Throwable { // NOPMD
        return callback.intercept(clientOfTheCallbackMock, method, args, null);
    }

    private static CallContext anyContext() {
        return (CallContext) anyObject();
    }

    private static CallTarget anyTarget() {
        return (CallTarget) anyObject();
    }

}
