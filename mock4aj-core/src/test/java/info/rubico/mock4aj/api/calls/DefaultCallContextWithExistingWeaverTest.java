package info.rubico.mock4aj.api.calls;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import info.rubico.mock4aj.api.weaving.Weaver;

import java.util.Date;

import org.aspectj.lang.annotation.Aspect;
import org.junit.Before;
import org.junit.Test;

public class DefaultCallContextWithExistingWeaverTest {

    private CallContext context;
    private Weaver weaverMock;

    @Before
    public void setupAspectJWeavingCallContext() {
        weaverMock = mock(Weaver.class);
        context = new DefaultCallContextWithExistingWeaver(weaverMock);
    }

    @Test
    public void whenWithAspectShouldRegisterTheAspectOnTheWeaver() {
        Class<?> theAspect = AnAspect.class;
        context.withAspect(theAspect);

        verify(weaverMock).registerAspect(theAspect);
    }

    @Test
    public void givenAlreadyAddedAspectWhenWithOtherAspectThenShouldRegister() {
        context.withAspect(AnAspect.class);
        context.withAspect(OtherAspect.class);

        verify(weaverMock).registerAspect(OtherAspect.class);
    }

    @Test
    public void whenWithAspectMultipleTimesThenShouldNotResetAndShouldRegisterEveryAspect() {
        context.withAspect(AnAspect.class);
        context.withAspect(OtherAspect.class);

        verify(weaverMock, times(2)).registerAspect((Class<?>) anyObject());
        verifyNoMoreInteractions(weaverMock);
    }

    @Test
    public void whenFromClassThenTheSourceIsSetToTheSameTypeAndName() {
        context.from(Date.class);
        CallSource source = context.getConfiguredCallSource();

        assertSame(Date.class, source.getType());
        assertEquals(Date.class.getName(), source.getName());
    }

    @Test
    public void whenFromSourceThenTheSameSourceIsReturned() {
        CallSource specifiedSource = mock(CallSource.class);
        context.from(specifiedSource);
        assertSame(specifiedSource, context.getConfiguredCallSource());
    }

    @Aspect
    public static class AnAspect {

    }

    @Aspect
    public static class OtherAspect {

    }
}
