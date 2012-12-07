package info.rubico.mock4aj.api.calls;

import java.lang.reflect.Method;

/**
 * A target for a simulated call.
 * 
 * The target is verified in the constructor so it is not possible to target a method that doesn't
 * exist or is not publicly callable.
 */
public abstract class CallTarget {

    /**
     * @throws NoSuchMethodException If the target is invalid
     */
    protected CallTarget(Object targetInstance,
                         String targetMethodName,
                         Class<?>[] argumentTypes)
        throws NoSuchMethodException {

        checkTargetValidity(targetInstance, targetMethodName, argumentTypes);
    }

    protected abstract void checkTargetValidity(Object targetInstance,
                                                String targetMethodName,
                                                Class<?>[] argumentTypes)
        throws NoSuchMethodException;

    public abstract Object getTargetInstance();

    public abstract Class<?> getTargetClass();

    public abstract String getTargetMethodName();

    public abstract Method getTargetMethod();

    public abstract Class<?>[] getArgumentTypes();

}
