package info.rubico.mock4aj.codegen.cglib.calls;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import info.rubico.mock4aj.api.calls.CallContext;
import info.rubico.mock4aj.api.exceptions.Mock4AjException;
import info.rubico.mock4aj.codegen.cglib.calls.caller.CglibWeavedCallerGenerator;
import info.rubico.mock4aj.codegen.cglib.calls.selector.CglibMethodSelectorGenerator;
import info.rubico.mock4aj.codegen.cglib.calls.selector.CglibSimulateCallInterceptor;
import info.rubico.mock4aj.internal.calls.CallerGenerator;

import java.util.Date;

import net.sf.cglib.proxy.MethodInterceptor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class CglibCallWeavingSimulatorTest { // NOPMD

    private Date targetMock;
    private Date targetSelectorMock;
    private CglibMethodSelectorGenerator targetSelectorGeneratorMock;
    private CallContext contextMock;
    private CallerGenerator callerGeneratorMock;

    private CglibCallWeavingSimulator simulator;

    @Before
    public void setupAWeavedCallSimulator() {
        targetMock = mock(Date.class);

        targetSelectorMock = mock(Date.class);
        targetSelectorGeneratorMock = mock(CglibMethodSelectorGenerator.class);
        when(targetSelectorGeneratorMock.generateSelector(same(targetMock), anyCallback()))
            .thenReturn(targetSelectorMock);

        contextMock = mock(CallContext.class);

        callerGeneratorMock = mock(CallerGenerator.class);

        simulator = new CglibCallWeavingSimulator(targetSelectorGeneratorMock,
                                                  callerGeneratorMock);
    }

    @Test
    public void byDefaultCreateATargetSelectorGenerator() {
        CglibCallWeavingSimulator newSimulator = new CglibCallWeavingSimulator();
        assertThat(newSimulator.getTargetSelectorGenerator(),
                   instanceOf(CglibMethodSelectorGenerator.class));
    }

    @Test
    public void byDefaultCreateACglibWeavedCallerGenerator() {
        CglibCallWeavingSimulator newSimulator = new CglibCallWeavingSimulator();
        assertThat(newSimulator.getCallerGenerator(),
                   instanceOf(CglibWeavedCallerGenerator.class));
    }

    /**
     * Rational: The tester might expect exceptions coming from the target (the desired behavior),
     * so errors from Mock4Aj should not interfere since the call is often done right after the
     * callTo like: callTo().m()
     */
    @Test
    public void givenAnErrorWhenCallThenShoudBeBoxedIntoAnMock4AjException() {
        Exception originalException = new RuntimeException(); // NOPMD
        doThrow(originalException).when(targetSelectorGeneratorMock)
            .generateSelector(anyObject(), anyCallback());

        try {
            simulator.call(targetMock, contextMock);
            fail("Should box the exception into a Mock4AjException");
        }
        catch (Mock4AjException exception) {
            assertSame(exception.getCause(), originalException);
        }
    }

    @Test
    public void whenCallThenATargetSelectorIsCreatedForTheCallTarget() {
        Object targetSelector = simulator.call(targetMock, contextMock);

        assertSame(targetSelector, targetSelectorMock);
        verify(targetSelectorGeneratorMock).generateSelector(same(targetMock), anyCallback());
    }

    @Test
    public void givenACallWhenCallToDifferentTargetThenShouldCreateNewSelector() {
        simulator.call(targetMock, contextMock);

        Date otherTarget = mock(Date.class);
        simulator.call(otherTarget, contextMock);

        verify(targetSelectorGeneratorMock, times(2)).generateSelector(anyObject(), anyCallback());
    }

    @Test
    public void givenACallWhenCallToDifferentContextThenShouldCreateNewSelector() {
        simulator.call(targetMock, contextMock);

        CallContext otherContext = mock(CallContext.class);
        simulator.call(targetMock, otherContext);

        verify(targetSelectorGeneratorMock, times(2)).generateSelector(anyObject(), anyCallback());
    }

    @Test
    public void whenCallThenTheSelectorIsConfiguredToSimulateACallToTheTarget() {
        simulator.call(targetMock, contextMock);

        ArgumentCaptor<CglibSimulateCallInterceptor> actionCaptor;
        actionCaptor = ArgumentCaptor.forClass(CglibSimulateCallInterceptor.class);
        verify(targetSelectorGeneratorMock).generateSelector(anyObject(), actionCaptor.capture());

        CglibSimulateCallInterceptor action = actionCaptor.getValue();
        assertSame(targetMock, action.getTargetObject());
    }

    @Test
    public void whenCallThenTheSelectorIsConfiguredToSimulateACallFromTheContext() {
        simulator.call(targetMock, contextMock);

        ArgumentCaptor<CglibSimulateCallInterceptor> actionCaptor;
        actionCaptor = ArgumentCaptor.forClass(CglibSimulateCallInterceptor.class);
        verify(targetSelectorGeneratorMock).generateSelector(anyObject(), actionCaptor.capture());

        CglibSimulateCallInterceptor action = actionCaptor.getValue();
        assertSame(contextMock, action.getContext());
    }

    @Test
    public void whenCallThenTheSelectorIsConfiguredToUseTheCallerGenerator() {
        simulator.call(targetMock, contextMock);

        ArgumentCaptor<CglibSimulateCallInterceptor> actionCaptor;
        actionCaptor = ArgumentCaptor.forClass(CglibSimulateCallInterceptor.class);
        verify(targetSelectorGeneratorMock).generateSelector(anyObject(), actionCaptor.capture());

        CglibSimulateCallInterceptor action = actionCaptor.getValue();
        assertSame(callerGeneratorMock, action.getCallerGenerator());
    }

    public static MethodInterceptor anyCallback() {
        return (MethodInterceptor) anyObject();
    }
}