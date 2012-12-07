package info.rubico.mock4aj.api.calls;

import info.rubico.mock4aj.api.weaving.Weaver;

public interface CallContext {

    /**
     * Adds an aspect to the context.
     */
    void withAspect(Class<?> aspectClass);

    Weaver getConfiguredWeaver();

    void from(Class<?> sourceType);

    void from(CallSource source);

    CallSource getConfiguredCallSource();
}
