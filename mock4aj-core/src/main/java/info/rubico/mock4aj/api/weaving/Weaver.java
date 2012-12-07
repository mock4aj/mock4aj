package info.rubico.mock4aj.api.weaving;

import info.rubico.mock4aj.api.exceptions.NotAConcreteAspect;
import info.rubico.mock4aj.api.exceptions.NotAnAspect;
import info.rubico.mock4aj.api.exceptions.WeavingError;

/**
 * Adapter that represents and pilot the weaver that will be responsible to weave classes with the
 * registered aspects.
 */
public interface Weaver {

    /**
     * Register an aspect to be weaved later into classes.
     */
    void registerAspect(Class<?> aspect);

    /**
     * Unregister a previously registered aspect. If the given aspect was not registered, nothing is
     * done.
     * 
     * @throws NotAnAspect if the class is not an aspect class.
     * @throws NotAConcreteAspect if the aspect is an abstract aspect.
     */
    void unregisterAspect(Class<?> aspect);

    /**
     * <p>
     * Reset the weaver to a neutral state (not always the initial state).
     * </p>
     * <ul>
     * <li>All registered aspects <strong>will be unregistered</strong>.</li>
     * <li>The exact neutral state depends on the implementation. Some states and caches could be
     * kept for performance reasons. Normally, those remainings should be safe.</li>
     * </ul>
     */
    void reset();

    /**
     * Weave a class' bytcode using all registered aspects.
     * 
     * @throws WeavingError An error occurs during the weaving.
     * @return The weaved class' bytecode.
     */
    byte[] weaveClassBytes(String className, final byte[] bytes);

}
