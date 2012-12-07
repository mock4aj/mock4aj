package info.rubico.mock4aj.codegen.cglib.calls.caller;

import info.rubico.mock4aj.api.calls.CallTarget;
import info.rubico.mock4aj.api.calls.MethodCaller;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;

/**
 * Default naming policy for a {@link MethodCallerBytecodeGenerator}.
 */
public class MethodCallerNamingPolicy implements NamingPolicy {

    private static final String DEFAULT_SOURCE = MethodCaller.class.getName();
    public static final String CALLER_TAG = "CallerByMock4Aj";
    public static final String ENHANCED_TAG = "_Enhanced";

    private final CallTarget target;

    public MethodCallerNamingPolicy(CallTarget target) {
        super();
        this.target = target;
    }

    public String getClassName(String prefix, String source, Object key, Predicate names) {
        String prefixTag = getPrefixTag(prefix);
        String targetDescription = getTargetDescription();
        String base = getBaseName(key, prefixTag, targetDescription);
        return getUniqueName(base, names);
    }

    protected String getPrefixTag(String prefix) {
        String prefixTag = prefix;
        if (prefixTag == null) {
            prefixTag = DEFAULT_SOURCE;
        }
        else if (prefixTag.startsWith("java")) {
            prefixTag = "$" + prefixTag; // NOPMD
        }
        return prefixTag;
    }

    protected String getTargetDescription() {
        String targetDescription = String.format("%s$$%s$%s",
                                                 getCallerTag(),
                                                 getTargetClassTag(),
                                                 target.getTargetMethodName());
        return targetDescription;
    }

    protected String getBaseName(Object key, String prefixTag, String targetDescription) {
        String base = String.format("%s$$%s$$%s",
                                    prefixTag,
                                    targetDescription,
                                    Integer.toHexString(key.hashCode()));

        return base;
    }

    protected String getUniqueName(String base, Predicate names) {
        String attempt = base;
        int index = 2;
        while (names.evaluate(attempt)) {
            attempt = base + "_" + index++;
        }
        return attempt;
    }

    protected String getCallerTag() {
        return CALLER_TAG;
    }

    protected String getTargetClassTag() {
        String simpleClassName = target.getTargetClass().getSimpleName();
        if (isAnEnhancedClass()) {
            simpleClassName = String.format("%s%s",
                                            getEnhancedSimpleClassName(simpleClassName),
                                            ENHANCED_TAG);
        }
        return simpleClassName;
    }

    protected boolean isAnEnhancedClass() {
        return target.getTargetClass().getName().contains("$$");
    }

    protected String getEnhancedSimpleClassName(String simpleClassName) {
        return simpleClassName.substring(0, simpleClassName.indexOf("$$"));
    }

    public int hashCode() {
        // CHECKSTYLE:OFF
        int hash = 7;
        hash = 31 * hash + target.hashCode();
        hash = 31 * hash + getCallerTag().hashCode();
        // CHECKSTYLE:ON
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MethodCallerNamingPolicy) {
            MethodCallerNamingPolicy oCaller = (MethodCallerNamingPolicy) obj;
            return (oCaller.getCallerTag().equals(getCallerTag()) && oCaller.target.equals(target));
        }
        return false;
    }
}