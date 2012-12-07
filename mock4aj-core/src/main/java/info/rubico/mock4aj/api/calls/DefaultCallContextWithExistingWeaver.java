package info.rubico.mock4aj.api.calls;

import info.rubico.mock4aj.api.weaving.Weaver;

public class DefaultCallContextWithExistingWeaver implements CallContext {

    private final Weaver weaver;
    private CallSource source;

    /**
     * WARNING: Be careful if the {@link DefaultCallContextWithExistingWeaver} is created with an
     * already used weaver: the weaver won't be reseted. The context only registered new aspects on
     * it.
     */
    public DefaultCallContextWithExistingWeaver(Weaver weaver) {
        this.weaver = weaver;
        this.source = new ExistingSource(MethodCaller.class);
    }

    public void withAspect(Class<?> aspectClass) {
        weaver.registerAspect(aspectClass);
    }

    public void from(Class<?> sourceType) {
        source = new ExistingSource(sourceType);
    }

    public void from(CallSource source) {
        this.source = source;
    }

    public Weaver getConfiguredWeaver() {
        return weaver;
    }

    public CallSource getConfiguredCallSource() {
        return source;
    }

}
