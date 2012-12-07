package info.rubico.mock4aj.codegen.cglib.proxies;

import info.rubico.mock4aj.api.exceptions.Mock4AjException;
import info.rubico.mock4aj.api.exceptions.NotAnInstanceToProxy;
import info.rubico.mock4aj.api.exceptions.UnproxiableType;
import info.rubico.mock4aj.api.proxies.WeavedProxyFactory;
import info.rubico.mock4aj.api.weaving.Weaver;
import info.rubico.mock4aj.codegen.cglib.classutils.CglibNearestRealSuperclassFinder;
import info.rubico.mock4aj.codegen.cglib.transformations.CglibWeavingGeneratorStrategy;
import info.rubico.mock4aj.internal.classutils.ClassUtils;
import info.rubico.mock4aj.internal.classutils.SuperclassFinder;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;

import org.objenesis.ObjenesisStd;

/**
 * {@link WeavedProxyFactory} implementation using Cglib.
 * 
 * <p>
 * To dump generated classes for debugging, set the
 * {@code net.sf.cglib.core.DebuggingClassWriter.DEBUG_LOCATION_PROPERTY} to a path.
 * </p>
 * 
 * @see WeavedProxyFactory
 */
public class CglibWeavedProxyFactory implements WeavedProxyFactory {

    private SuperclassFinder superclassFinder = CglibNearestRealSuperclassFinder.INSTANCE;

    public <T> T createWeavedProxy(final T objectToProxy, final Weaver weaver) {
        Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(superclassFinder.findSuperclass(objectToProxy));
        enhancer.setInterfaces(objectToProxy.getClass().getInterfaces());

        enhancer.setUseCache(false); // Caching doesn't work well with weaving.
        enhancer.setNamingPolicy(WeavedProxyNamingPolicy.INSTANCE);
        enhancer.setStrategy(createGeneratorStrategy(weaver));
        enhancer.setCallbackType(ProxyMethodCallback.class);

        return createProxyObject(objectToProxy, enhancer);
    }

    protected CglibWeavingGeneratorStrategy createGeneratorStrategy(final Weaver weaverAdapter) {
        return new CglibWeavingGeneratorStrategy(weaverAdapter);
    }

    private <T> T createProxyObject(final T objectToProxy, Enhancer enhancer) {
        try {
            Class<?> proxyClass = enhancer.createClass();
            return instantiate(objectToProxy, proxyClass);
        }
        catch (Exception e) {
            throw handleCreationException(objectToProxy, e);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T instantiate(final T objectToProxy, Class<?> proxyClass) {
        Factory proxyObject = (Factory) new ObjenesisStd().newInstance(proxyClass);
        proxyObject.setCallbacks(new Callback[] { createCallback(objectToProxy) });
        return (T) proxyObject;
    }

    protected <T> ProxyMethodCallback createCallback(final T objectToProxy) {
        return new ProxyMethodCallback(objectToProxy);
    }

    private RuntimeException handleCreationException(final Object objectToProxy, Exception error) {
        Class<?> clazz = objectToProxy.getClass();
        if (clazz.isInstance(Class.class)) {
            throw new NotAnInstanceToProxy(error);
        }
        else if (ClassUtils.isFinalClass(clazz) || clazz.isPrimitive()) {
            throw new UnproxiableType(clazz, error);
        }
        throw new Mock4AjException("An error occurs during the creation of the proxy. It "
                                   + "may be caused by a special object type for which "
                                   + "the way we create proxies is not suitable.", error);
    }

    public void setSuperclassFinder(SuperclassFinder superclassFinder) {
        this.superclassFinder = superclassFinder;
    }
}
