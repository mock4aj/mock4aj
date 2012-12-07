package info.rubico.mock4aj;

import info.rubico.mock4aj.api.calls.CallContext;
import info.rubico.mock4aj.api.calls.DefaultCallContextWithExistingWeaver;
import info.rubico.mock4aj.api.calls.FictitiousSource;
import info.rubico.mock4aj.api.proxies.WeavedProxyFactory;
import info.rubico.mock4aj.api.weaving.Weaver;
import info.rubico.mock4aj.codegen.cglib.calls.CglibCallWeavingSimulator;
import info.rubico.mock4aj.codegen.cglib.proxies.CglibWeavedProxyFactory;
import info.rubico.mock4aj.internal.WeavedProxyFacade;
import info.rubico.mock4aj.weavers.aspectj.AspectJWeaver;

/**
 * Mock4AspectJ is the main interface that provides convenient functions in a simple and
 * comprehensive language to test aspects, control the weaving and define some contexts.
 * <p>
 * The goal of this static facade is to be able to write tests in a more readable syntax that
 * expressed clearly the meaning of the test.
 * <p>
 * <h2>Warning</h2>
 * Mock4Aj is not fully thread-safe since objects produced and returned are not synchronized.
 * Anyway, because Mock4AJ is intended to be used for unit tests, it should not be an issue since it
 * is generally considered as a good practice to keep unit test independent from threads.
 */
public final class Mock4AspectJ {

    private static final WeavedProxyFactory DEFAULT_PROXY_FACTORY = new CglibWeavedProxyFactory();
    private static final Weaver DEFAULT_WEAVER = new AspectJWeaver();

    private static WeavedProxyFacade weavedProxyFacade;

    static {
        weavedProxyFacade = new WeavedProxyFacade(DEFAULT_PROXY_FACTORY, DEFAULT_WEAVER);
    }

    private Mock4AspectJ() {
    }

    public static synchronized <T> T createWeavedProxy(T objectToProxy,
                                                       Class<?>... aspectsToWeave) {
        return weavedProxyFacade.createWeavedProxy(objectToProxy, aspectsToWeave);
    }

    public static synchronized <T> T call(T objectToCall, CallContext callContext) {
        return new CglibCallWeavingSimulator().call(objectToCall, callContext);
    }

    public static synchronized CallContext callContext() {
        DEFAULT_WEAVER.reset();
        return new DefaultCallContextWithExistingWeaver(DEFAULT_WEAVER);
    }

    public static synchronized FictitiousSource fakeSourceClass(String name) {
        return new FictitiousSource(name);
    }
}
