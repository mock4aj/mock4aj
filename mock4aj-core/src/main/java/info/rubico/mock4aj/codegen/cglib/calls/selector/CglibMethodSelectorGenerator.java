package info.rubico.mock4aj.codegen.cglib.calls.selector;

import info.rubico.mock4aj.api.exceptions.Mock4AjException;
import info.rubico.mock4aj.api.exceptions.UncallableType;
import info.rubico.mock4aj.codegen.cglib.classutils.CglibNearestRealSuperclassFinder;
import info.rubico.mock4aj.internal.MethodSelector;
import info.rubico.mock4aj.internal.classutils.ClassUtils;
import info.rubico.mock4aj.internal.classutils.SuperclassFinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;

import org.objenesis.ObjenesisStd;

/**
 * Generate a method selector on a given object.
 * <p>
 * A Selector is a dynamically generated class that have the same methods signatures than a template
 * object but with every methods replaced with stubs. A selector is used to select a method by
 * invoking it on a fake object without executing the real method behavior.
 * <p>
 * The generator is configured with a {@code callback}. An action is a {@link MethodInterceptor}
 * that contains the code to be executed when the method is selected. Typically, it is what to do
 * when the given method is selected.
 * <h2>Example</h2>
 * 
 * <pre>
 * date = new Date();
 * generator = new MethodSelectorGenerator();
 * generator.generateSelector(date, action).setTime(5L); 
 *     --> will call the action with setTime(5L).
 *     --> the real date object WON'T be call.
 * </pre>
 */
public class CglibMethodSelectorGenerator {

    private SuperclassFinder superclassFinder = CglibNearestRealSuperclassFinder.INSTANCE;

    public <T> T generateSelector(T templateObject, MethodInterceptor actionWhenCall) {
        Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(superclassFinder.findSuperclass(templateObject));
        enhancer.setInterfaces(determineInterfacesFor(templateObject));
        enhancer.setNamingPolicy(new MethodSelectorNamingPolicy());
        enhancer.setCallbackType(MethodInterceptor.class);

        try {
            Class<?> proxyClass = enhancer.createClass();
            return instantiate(proxyClass, actionWhenCall);
        }
        catch (Exception error) {
            throw handleCreationException(templateObject, error);
        }
    }

    protected Class<?>[] determineInterfacesFor(Object template) {
        Class<?>[] templateInterfaces = template.getClass().getInterfaces();
        List<Class<?>> interfaces = new ArrayList<Class<?>>(templateInterfaces.length + 1);
        interfaces.addAll(Arrays.asList(templateInterfaces));
        interfaces.add(MethodSelector.class);
        return interfaces.toArray(new Class<?>[interfaces.size()]);
    }

    @SuppressWarnings("unchecked")
    protected <T> T instantiate(Class<?> proxyClass, MethodInterceptor actionWhenCall) {
        Factory proxyObject = (Factory) new ObjenesisStd().newInstance(proxyClass);
        proxyObject.setCallbacks(new Callback[] { actionWhenCall });
        return (T) proxyObject;
    }

    private RuntimeException handleCreationException(Object templateObject, Exception error) {
        Class<?> clazz = templateObject.getClass();
        if (ClassUtils.isFinalClass(clazz)) {
            return new UncallableType(clazz, error);
        }
        return new Mock4AjException("Method selector error.\n"
                                    + "  An error occurs during the creation of the method "
                                    + "selector. It may be caused by a special object type for "
                                    + "which the way we create selectors is not suitable.",
                                    error);
    }

    public void setSuperclassFinder(SuperclassFinder superclassFinder) {
        this.superclassFinder = superclassFinder;
    }

    /**
     * Default naming policy for a selector. It adds a tag indicating that this object is a selector
     * created by Mock4Aj.
     */
    private class MethodSelectorNamingPolicy extends DefaultNamingPolicy {
        @Override
        protected String getTag() {
            return "MethodSelectorByMock4Aj";
        }
    }
}