package info.rubico.mock4aj.weavers.aspectj;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import info.rubico.mock4aj.api.exceptions.Mock4AjException;
import info.rubico.mock4aj.api.exceptions.NotAConcreteAspect;
import info.rubico.mock4aj.api.exceptions.NotAnAspect;
import info.rubico.mock4aj.api.exceptions.WeavingError;

import java.io.IOException;
import java.util.Date;

import org.aspectj.lang.annotation.Aspect;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unused")
public class AspectJWeaverTest { // NOPMD

    private AspectJWeaver weaverAdapter;
    private DynamicRuntimeWeavingAdaptor ajMocked;

    @Before
    public void configureAJWeaverAdapter() {
        ajMocked = mock(DynamicRuntimeWeavingAdaptor.class);

        weaverAdapter = new AspectJWeaver(ajMocked);
    }

    @Test(expected = NotAnAspect.class)
    public void givenNonAspectClassWhenRegisterThenShouldThrowNotAnAspect() {
        Class<?> nonAspectClass = Date.class;
        givenAjWillThrowRuntimeExceptionOn(nonAspectClass);
        givenAjWillReturnIsAspect(nonAspectClass, false);

        weaverAdapter.registerAspect(nonAspectClass);
    }

    @Test(expected = NotAConcreteAspect.class)
    public void givenAbstractAspectWhenRegisterThenShouldThrowNotAConcreteAspect() {
        Class<?> abstractAspect = AbstractAspect.class;
        givenAjWillReturnIsAspect(abstractAspect, true);

        weaverAdapter.registerAspect(abstractAspect);
    }

    @Test(expected = NotAConcreteAspect.class)
    public void givenAnAbstractNonAspectClassWhenRegisterShouldThrowNotAConcreteAspect() {
        Class<?> abstractClass = AbstractClass.class;
        givenAjWillThrowRuntimeExceptionOn(abstractClass);
        givenAjWillReturnIsAspect(abstractClass, false);

        weaverAdapter.registerAspect(abstractClass);
    }

    @Test(expected = Mock4AjException.class)
    public void givenARuntimeErrorFromAjWhenRegisterThenShouldThrowMockAjException() {
        Class<?> anAspect = AnAspect.class;
        givenAjWillThrowRuntimeExceptionOn(anAspect);
        givenAjWillReturnIsAspect(anAspect, true);

        weaverAdapter.registerAspect(anAspect);
    }

    @Test
    public void givenNotRegisteredAspectWhenRegisterThenIsAddedToAJWeaver() {
        weaverAdapter.registerAspect(AnAspect.class);
        verify(ajMocked).registerAspect(AnAspect.class.getName());
    }

    @Test
    public void givenAlreadyRegisteredAspectWhenRegisterThenNothingIsDone() {
        weaverAdapter.registerAspect(AnAspect.class);
        verify(ajMocked).registerAspect(anyString());

        weaverAdapter.registerAspect(AnAspect.class);

        verifyNoMoreInteractions(ajMocked);
    }

    @Test
    public void givenARegisteredAspectWhenUnregisterThenIsRemovedFromAJWeaver() {
        weaverAdapter.registerAspect(AnAspect.class);
        weaverAdapter.unregisterAspect(AnAspect.class);
        verify(ajMocked).unregisterAspect(AnAspect.class.getName());
    }

    @Test
    public void givenAnUnRegisteredAspectWhenUnregisterThenNothingIsDone() {
        weaverAdapter.registerAspect(AnAspect.class);
        weaverAdapter.unregisterAspect(AnAspect.class);

        weaverAdapter.unregisterAspect(AnAspect.class);
        verify(ajMocked, times(1)).unregisterAspect(AnAspect.class.getName());
    }

    @Test
    public void givenAnUnregisteredAspectWhenRegisterThenIsAdded() {
        weaverAdapter.registerAspect(AnAspect.class);
        weaverAdapter.unregisterAspect(AnAspect.class);

        weaverAdapter.registerAspect(AnAspect.class);

        verify(ajMocked, times(2)).registerAspect(AnAspect.class.getName());
    }

    @Test
    public void whenResetThenShouldResetAjWeaver() {
        weaverAdapter.reset();
        verify(ajMocked).reset();
    }

    @Test
    public void givenResetedAspectsWhenRegisterThenIsAddedAgain() {
        weaverAdapter.registerAspect(AnAspect.class);
        weaverAdapter.reset();

        weaverAdapter.registerAspect(AnAspect.class);

        verify(ajMocked, times(2)).registerAspect(AnAspect.class.getName());
    }

    @Test
    public void whenWeaveTheShouldCallWeaveOnTheAJWeaver() throws IOException {
        weaverAdapter.registerAspect(AnAspect.class);
        String classToWeaveName = "SomeClass";
        byte[] classToWeaveBytecode = "Some bytecode".getBytes();

        weaverAdapter.weaveClassBytes(classToWeaveName, classToWeaveBytecode);

        verify(ajMocked).weaveClass(classToWeaveName, classToWeaveBytecode);
    }

    @Test
    public void whenWeaveThenTheWeavedBytecodeShouldBeReturned() throws IOException {
        String className = "SomeClass";
        byte[] orginialBytecode = "Some bytecode".getBytes();
        byte[] weavedBytecode = "Other bytecode".getBytes();
        given(ajMocked.weaveClass(className, orginialBytecode)).willReturn(weavedBytecode);

        weaverAdapter.registerAspect(AnAspect.class);
        byte[] newBytecode = weaverAdapter.weaveClassBytes(className, orginialBytecode);

        assertEquals(weavedBytecode, newBytecode);
    }

    @Test(expected = WeavingError.class)
    public void givenAnyErrorWhenWeaveThenShouldThrowWeavingError() throws IOException {
        Throwable anException = new RuntimeException("Something goes wrong"); // NOPMD
        String aClassName = "AClass";
        byte[] someBytes = "Some bytes".getBytes();
        given(ajMocked.weaveClass(aClassName, someBytes)).willThrow(anException);

        weaverAdapter.registerAspect(AnAspect.class);
        weaverAdapter.weaveClassBytes(aClassName, someBytes);
    }

    @Test
    public void givenNoRegisteredAspectWhenWeaveThenNothingIsDone() throws IOException {
        String aClassName = "AClass";
        byte[] someBytes = "Some bytes".getBytes();

        weaverAdapter.weaveClassBytes(aClassName, someBytes);

        verify(ajMocked, never()).weaveClass(aClassName, someBytes);
    }

    private void givenAjWillThrowRuntimeExceptionOn(Class<?> clazz) {
        willThrow(new RuntimeException()) // NOPMD
            .given(ajMocked)
            .registerAspect(clazz.getName());
    }

    private void givenAjWillReturnIsAspect(Class<?> clazz, boolean isAspect) {
        willReturn(isAspect)
            .given(ajMocked)
            .isAspect(clazz);
    }

    @Aspect
    public static class AnAspect {
    }

    @Aspect
    public static class OtherAspect {
    }

    @Aspect
    public abstract static class AbstractAspect { // NOPMD
    }

    public abstract class AbstractClass { // NOPMD
    }

}
