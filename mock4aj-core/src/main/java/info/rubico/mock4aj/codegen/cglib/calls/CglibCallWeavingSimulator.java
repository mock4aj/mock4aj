package info.rubico.mock4aj.codegen.cglib.calls;

import info.rubico.mock4aj.api.calls.CallContext;
import info.rubico.mock4aj.api.calls.CallSimulator;
import info.rubico.mock4aj.api.exceptions.Mock4AjException;
import info.rubico.mock4aj.codegen.cglib.calls.caller.CglibWeavedCallerGenerator;
import info.rubico.mock4aj.codegen.cglib.calls.selector.CglibMethodSelectorGenerator;
import info.rubico.mock4aj.codegen.cglib.calls.selector.CglibSimulateCallInterceptor;
import info.rubico.mock4aj.internal.calls.CallerGenerator;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Cglib implementation of a {@link CallSimulator}.
 * 
 * @see CallSimulator
 */
public class CglibCallWeavingSimulator implements CallSimulator {

    // @formatter:off
    private static final String CALL_TO_ERROR_MESSAGE = "An error occurs during the creation of "
                                             + "the Call Target Selector. This error is not "
                                             + "managed, please report it to the mailing list.";
    // @formatter:on

    private final CglibMethodSelectorGenerator targetSelectorGenerator;
    private final CallerGenerator callerGenerator;

    public CglibCallWeavingSimulator() {
        targetSelectorGenerator = new CglibMethodSelectorGenerator();
        callerGenerator = new CglibWeavedCallerGenerator();
    }

    public CglibCallWeavingSimulator(CglibMethodSelectorGenerator targetSelectorGenerator,
                                     CallerGenerator callerGenerator) {
        this.targetSelectorGenerator = targetSelectorGenerator;
        this.callerGenerator = callerGenerator;
    }

    public <T> T call(T targetObject, CallContext context) {
        try {
            MethodInterceptor actionOnSelection = new CglibSimulateCallInterceptor(targetObject,
                                                                                   context,
                                                                                   callerGenerator);
            return targetSelectorGenerator.generateSelector(targetObject, actionOnSelection);
        }
        catch (Exception originalException) {
            throw new Mock4AjException(CALL_TO_ERROR_MESSAGE, originalException);
        }
    }

    protected CglibMethodSelectorGenerator getTargetSelectorGenerator() {
        return targetSelectorGenerator;
    }

    protected CallerGenerator getCallerGenerator() {
        return callerGenerator;
    }

}
