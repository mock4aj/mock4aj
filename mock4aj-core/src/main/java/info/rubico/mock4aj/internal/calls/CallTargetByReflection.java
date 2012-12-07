package info.rubico.mock4aj.internal.calls;

import info.rubico.mock4aj.api.calls.CallTarget;

import java.lang.reflect.Method;

/**
 * A call target that uses Java 1.5 Reflection.
 * 
 * @see CallTarget
 */
public class CallTargetByReflection extends CallTarget {

    // @formatter:off
    private static final String NULL_TARGET_MESSAGE = "Null instance is not a valid target";
    private static final String NOT_PUBLIC_METHOD_MESSAGE =  "The method exists but is not"
                                                             + "publicly callable.";
    // @formatter:on

    private Object targetInstance;
    private Method targetMethod;

    /**
     * @see CallTarget#CallTarget(Object, String, Class...)
     */
    public CallTargetByReflection(Object targetInstance,
                                  String targetMethodName,
                                  Class<?>... argumentTypes)
        throws NoSuchMethodException {

        super(targetInstance, targetMethodName, argumentTypes);
    }

    @Override
    protected void checkTargetValidity(Object targetInstance,
                                       String targetMethodName,
                                       Class<?>[] argumentTypes)
        throws NoSuchMethodException {

        if (targetInstance == null) {
            throw new NoSuchMethodException(NULL_TARGET_MESSAGE);
        }

        this.targetInstance = targetInstance;
        this.targetMethod = findTargetMethod(targetMethodName, argumentTypes);
    }

    private Method findTargetMethod(String targetMethodName, Class<?>... argumentTypes)
        throws NoSuchMethodException {

        try {
            return getTargetClass().getMethod(targetMethodName, argumentTypes);
        }
        catch (NoSuchMethodException error) {
            getTargetClass().getDeclaredMethod(targetMethodName, argumentTypes);
            throw new NoSuchMethodException(NOT_PUBLIC_METHOD_MESSAGE); // NOPMD
        }
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Object getTargetInstance() {
        return targetInstance;
    }

    public Class<?> getTargetClass() {
        return targetInstance.getClass();
    }

    public String getTargetMethodName() {
        return targetMethod.getName();
    }

    public Class<?>[] getArgumentTypes() {
        return targetMethod.getParameterTypes();
    }

}
