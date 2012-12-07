package info.rubico.mock4aj.api.calls;

import info.rubico.mock4aj.api.exceptions.EncapsulatedExceptionThrownByTarget;

/**
 * A dynamically generated class that simulate a call to a method (the {@link CallTarget}) in a
 * certain context ({@link CallContext}).
 * <p>
 * The main usage is to simulate calls that will be weaved and intercepted by a weaver. Since the
 * call is hard-coded (forged into the Bytecode) and don't use proxies, this will match "call"
 * pointcuts. So testing those pointcuts become possible.
 * <p>
 * The custom calling code is generated inside the {@link #doCall(Object[])} method. The target call
 * is hard-coded by the generator.
 * <p>
 * The generated class have a name according to the {@link CallSource} defined in the
 * {@link CallContext}. The class always implemented {@link MethodCaller}. It will also extends
 * and/or implements other classes and interfaces to reproduce the right {@link CallContext}.
 */
public interface MethodCaller {

    /**
     * Do the call to the target.
     * 
     * @throws IllegalArgumentException If {@code argumentValues} dont't match the target method
     *         arguments (wrong number, wrong type, ...).
     * @throws EncapsulatedExceptionThrownByTarget An exception throw by the target method and
     *         encapsulated.
     * @return The result of the call on the target. For void targets, null is returned.
     */
    Object doCall(Object[] argumentValues);
}
