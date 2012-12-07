package info.rubico.mock4aj.internal.calls;

import info.rubico.mock4aj.api.calls.CallContext;
import info.rubico.mock4aj.api.calls.CallTarget;
import info.rubico.mock4aj.api.calls.MethodCaller;

/**
 * Generates dynamically and at runtime a {@link MethodCaller} that simulate a call to the
 * {@link CallTarget} coming from a {@link CallContext}.
 */
public interface CallerGenerator {

    MethodCaller generateMethodCaller(CallTarget target, CallContext callContext);

}
