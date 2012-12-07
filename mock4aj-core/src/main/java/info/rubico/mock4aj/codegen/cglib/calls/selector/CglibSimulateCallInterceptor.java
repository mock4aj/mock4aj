package info.rubico.mock4aj.codegen.cglib.calls.selector;

import info.rubico.mock4aj.api.calls.CallContext;
import info.rubico.mock4aj.api.calls.CallTarget;
import info.rubico.mock4aj.api.calls.MethodCaller;
import info.rubico.mock4aj.api.exceptions.EncapsulatedExceptionThrownByTarget;
import info.rubico.mock4aj.api.exceptions.Mock4AjException;
import info.rubico.mock4aj.internal.calls.CallTargetByReflection;
import info.rubico.mock4aj.internal.calls.CallerGenerator;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * A Cglib Class Generator Selection Action that creates a {@link MethodCaller} when a method is
 * called. The {@link MethodCaller} is generated using a {@link CallerGenerator} with the called
 * method as the target.
 */
public class CglibSimulateCallInterceptor implements MethodInterceptor {

    // @formatter:off
    private static final String CALL_UNKNOWN_ERROR_MESSAGE = "An error occurs when calling the "
                                                  + "targeted method on the generated caller "
                                                  + "class. \n" 
                                                  + "This error is not managed. Please report "
                                                  + "it to the mailing list.";
    // @formatter:on

    private final CallerGenerator callerGenerator;
    private final Object targetObject;
    private final CallContext context;

    public CglibSimulateCallInterceptor(Object targetObject,
                                        CallContext context,
                                        CallerGenerator callerGenerator) {
        this.targetObject = targetObject;
        this.context = context;
        this.callerGenerator = callerGenerator;
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
        throws Throwable {

        MethodCaller caller = generateCaller(method);
        return doTheCall(args, caller);
    }

    protected MethodCaller generateCaller(Method method) throws NoSuchMethodException {
        CallTarget target = new CallTargetByReflection(targetObject,
                                                       method.getName(),
                                                       method.getParameterTypes());
        return callerGenerator.generateMethodCaller(target, context);
    }

    protected Object doTheCall(Object[] args, MethodCaller caller) throws Throwable {
        try {
            return caller.doCall(args);
        }
        catch (EncapsulatedExceptionThrownByTarget exceptionFromTarget) {
            throw exceptionFromTarget.getCause();
        }
        catch (Mock4AjException mock4ajException) { // NOPMD
            throw mock4ajException;
        }
        catch (Exception callerException) {
            throw new Mock4AjException(CALL_UNKNOWN_ERROR_MESSAGE, callerException);
        }
    }

    public CallerGenerator getCallerGenerator() {
        return callerGenerator;
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public CallContext getContext() {
        return context;
    }
}