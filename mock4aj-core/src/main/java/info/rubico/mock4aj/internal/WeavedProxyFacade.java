package info.rubico.mock4aj.internal;

import info.rubico.mock4aj.api.proxies.WeavedProxyFactory;
import info.rubico.mock4aj.api.weaving.Weaver;

/**
 * Facade to offer a simplest entry point to the proxy weaving creation process. It uses the
 * {@link WeavedProxyFactory} but hides the {@link Weaver} management.
 */
public class WeavedProxyFacade {

    private final WeavedProxyFactory proxyFactory;
    private final Weaver weaver;

    public WeavedProxyFacade(final WeavedProxyFactory proxyFactory, final Weaver weaver) {
        this.proxyFactory = proxyFactory;
        this.weaver = weaver;
    }

    public <T> T createWeavedProxy(final T objectToProxy, final Class<?>... aspectsToWeave) {
        registerOnlyThoseAspectsForWeaving(aspectsToWeave);
        return proxyFactory.createWeavedProxy(objectToProxy, weaver);
    }

    private void registerOnlyThoseAspectsForWeaving(final Class<?>[] aspectsToWeave) {
        weaver.reset();
        for (Class<?> aspectToWeave : aspectsToWeave) {
            weaver.registerAspect(aspectToWeave);
        }
    }

    protected WeavedProxyFactory getWeavedProxyFactory() {
        return proxyFactory;
    }

    protected Weaver getWeaver() {
        return weaver;
    }

}
