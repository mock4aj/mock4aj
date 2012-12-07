package info.rubico.mock4aj.api.calls;

/**
 * Simulate calls originating from a class in a defined context to a target.
 * <p>
 * It will generate the {@link MethodCaller} to recreate the context and make the real call. The
 * simulator is some kind of <strong>on-the-fly {@link MethodCaller} factory</strong>.
 * <p>
 * The {@link #call(Object)} method return a selector (with the same signature as the target). When
 * a method is invoked on the selector, a real call will be done to the target but within the
 * context passed to the the simulator.
 * 
 * <h2>Example</h2>
 * 
 * To simulate a call to the Date.setTime method in a given context:
 * 
 * <pre>
 * CallContext context = new CallContextImplementation();
 * context.withSomeSonfiguration(VALAUE);
 * 
 * CallSimulator simulator = new CallSimulatorImplementation();
 * simulator.call(aDate, context).setTime(40L);
 * </pre>
 */
public interface CallSimulator {

    /**
     * Simulate a call in the given context to the method that will be invoked on the returned stub.
     * <p>
     * The returned stub object is used as a target selector and will use the method called on it as
     * the target. When the method is selected, a call will be simulated to the selected method
     * using a {@link MethodCaller}.
     * 
     * @param targetObject The object to be called.
     * @param callContext The context of the call (the call will originate from within that context)
     * @return A target selector that allows selecting the method to call on the target.
     */
    <T> T call(T targetObject, CallContext callContext);

}
