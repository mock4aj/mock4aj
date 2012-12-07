package info.rubico.mock4aj.internal;

import static org.mockito.Mockito.*;
import info.rubico.mock4aj.api.proxies.WeavedProxyFactory;
import info.rubico.mock4aj.api.weaving.Weaver;

import java.util.Date;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Before;
import org.junit.Test;

public class WeavedProxyFacadeTest {

    private WeavedProxyFacade core;
    private WeavedProxyFactory proxyFactoryMocked;
    private Date aMock;
    private Weaver weaverAdapterMocked;

    /**
     * An aspect that alters the behaviour of {@link RealClass} class. It change the behaviour of
     * {@code RealClass.returnFalse()} to return {@code true} instead.
     */
    @Aspect
    public static class AnAspect {

        @Around("execution(boolean RealClass.returnTrue(..)")
        public boolean returnTrueInsteadOfFalse() {
            return true;
        }

    }

    @Before
    public void setupTargetAndMocks() {
        aMock = mock(Date.class);

        weaverAdapterMocked = mock(Weaver.class);
        proxyFactoryMocked = mock(WeavedProxyFactory.class);

        core = new WeavedProxyFacade(proxyFactoryMocked, weaverAdapterMocked);
    }

    @Test
    public void whenCreateProxyThenTheProxyFactoryIsUsed() {
        core.createWeavedProxy(aMock, AnAspect.class);
        verify(proxyFactoryMocked).createWeavedProxy(aMock, weaverAdapterMocked);
    }

    @Test
    public void whenCreateWeavedProxyThenWeaverShouldBeReset() {
        core.createWeavedProxy(aMock, AnAspect.class);
        verify(weaverAdapterMocked).reset();
    }

    @Test
    public void givenAnAspectWhenCreateWeavedProxyThenItShouldBeRegistered() {
        Class<AnAspect> aspectToWeave = AnAspect.class;
        core.createWeavedProxy(aMock, aspectToWeave);
        verify(weaverAdapterMocked).registerAspect(aspectToWeave);
    }

}
