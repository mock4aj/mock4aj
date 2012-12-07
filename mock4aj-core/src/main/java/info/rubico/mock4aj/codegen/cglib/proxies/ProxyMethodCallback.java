package info.rubico.mock4aj.codegen.cglib.proxies;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Callback for Cglib that forwards calls to the object proxied. In fact a proxy of the class is
 * created but every calls will be forwarded to the same method on the object (instance) proxied.
 */
public class ProxyMethodCallback implements MethodInterceptor {

    private final Object objectToProxy;

    public ProxyMethodCallback(Object objectToProxy) {
        this.objectToProxy = objectToProxy;
    }

    public Object intercept(Object obj, Method methodInvoked,
                            Object[] args, MethodProxy proxy) throws Throwable
    {
        return proxy.invoke(objectToProxy, args);
    }
}