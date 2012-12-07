package info.rubico.mock4aj.weavers.aspectj;

import info.rubico.mock4aj.api.exceptions.Mock4AjException;
import info.rubico.mock4aj.api.exceptions.NotAConcreteAspect;
import info.rubico.mock4aj.api.exceptions.NotAnAspect;
import info.rubico.mock4aj.api.exceptions.WeavingError;
import info.rubico.mock4aj.api.weaving.Weaver;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.TreeSet;

import org.aspectj.bridge.context.CompilationAndWeavingContext;

/**
 * <p>
 * AspectJ Runtime Weaver Adapter.
 * </p>
 * <p>
 * It uses an extension to the AspectJ Runtime Weaver and the LTW World more suitable for the
 * weaving of multiple dynamic classes.
 * </p>
 * <p>
 * By default, a {@link DynamicRuntimeWeavingAdaptor} is created and initialised but another
 * implementation or an existing one could be set. But if an existing
 * {@link DynamicRuntimeWeavingAdaptor} is set, <strong>it must has been initialised
 * before</strong>.
 * </p>
 * 
 * @see DynamicRuntimeWeavingAdaptor
 * @see DynamicRuntimeWorld
 */
public class AspectJWeaver implements Weaver {

    private final DynamicRuntimeWeavingAdaptor ajWeaver;
    private final Set<String> registeredAspects = new TreeSet<String>();

    public AspectJWeaver() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ajWeaver = new DynamicRuntimeWeavingAdaptor(classLoader);
        ajWeaver.initialize();
    }

    public AspectJWeaver(DynamicRuntimeWeavingAdaptor ajWeaver) {
        this.ajWeaver = ajWeaver;
    }

    /**
     * @throws NotAnAspect if the class is not an aspect class.
     * @throws NotAConcreteAspect if the aspect is an abstract aspect.
     * @throws Mock4AjException if another error prevent the aspect to be registered.
     * @see Weaver
     */
    public synchronized void registerAspect(Class<?> aspect) {
        if (!registeredAspects.contains(aspect.getName())) {
            checkAspectIsConcrete(aspect);
            registerToAspectJ(aspect);
            registeredAspects.add(aspect.getName());
        }
    }

    protected void checkAspectIsConcrete(Class<?> aspect) {
        if (Modifier.isAbstract(aspect.getModifiers())) {
            throw new NotAConcreteAspect(aspect); // NOPMD
        }
    }

    protected void registerToAspectJ(Class<?> aspect) {
        try {
            ajWeaver.registerAspect(aspect.getName());
        }
        catch (Exception e) {
            if (!ajWeaver.isAspect(aspect)) {
                throw new NotAnAspect(aspect); // NOPMD
            }
            throw handleUnknownRegistrationError(aspect, e);
        }
    }

    protected Mock4AjException handleUnknownRegistrationError(Class<?> clazz, Exception cause) {
        // @formatter:off
        final String message = String.format(
                    "An error occurs during the registration of the class '%s' "
                    + "as an aspect. This error was reported by the AspectJ Weaver. "
                    + "It is probably because the class is not an aspect or because the "
                    + "aspect is not weavble.\n"
                    + "Look at the original exception or use the AspectJ logger "
                    + "to find the exact problem.", clazz);
        // @formatter:on
        return new Mock4AjException(message, cause);
    }

    public synchronized void unregisterAspect(Class<?> aspect) {
        final String aspectName = aspect.getName();
        if (registeredAspects.contains(aspectName)) {
            ajWeaver.unregisterAspect(aspectName);
            registeredAspects.remove(aspectName);
        }
    }

    public synchronized void reset() {
        ajWeaver.reset();
        registeredAspects.clear();
        CompilationAndWeavingContext.resetForThread();
    }

    public synchronized byte[] weaveClassBytes(String className, byte[] bytes) {
        try {
            if (!registeredAspects.isEmpty()) {
                return ajWeaver.weaveClass(className, bytes);
            }
            return bytes;
        }
        catch (Exception e) {
            throw handleWeavingException(className, e);
        }
    }

    protected WeavingError handleWeavingException(String className, Exception cause) {
        // @formatter:off
        final String message = String.format(
                    "An error occurs during the weaving of the class '%s'.\n"
                    + "This error was reported by the AspectJ Weaver. Look "
                    + "at the original exception or use the AspectJ logger "
                    + "to find the exact problem.", className);
        // @formatter:on
        return new WeavingError(message, cause);
    }

}
