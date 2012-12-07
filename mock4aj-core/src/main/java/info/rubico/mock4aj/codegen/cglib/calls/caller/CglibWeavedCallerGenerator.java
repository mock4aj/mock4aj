package info.rubico.mock4aj.codegen.cglib.calls.caller;

import info.rubico.mock4aj.api.calls.CallContext;
import info.rubico.mock4aj.api.calls.CallTarget;
import info.rubico.mock4aj.api.calls.MethodCaller;
import info.rubico.mock4aj.api.weaving.Weaver;
import info.rubico.mock4aj.codegen.cglib.transformations.CglibWeavingGeneratorStrategy;
import info.rubico.mock4aj.internal.calls.CallerGenerator;

/**
 * Cglib implementation of {@link CallerGenerator}.
 * <p>
 * It uses a {@link MethodCallerBytecodeGenerator} to generated a {@link MethodCaller} and weave it
 * with a {@link Weaver}.
 */
public class CglibWeavedCallerGenerator implements CallerGenerator {

    public MethodCaller generateMethodCaller(CallTarget target, CallContext context) {
        CglibWeavingGeneratorStrategy weavingStrategy = createWeavingStrategy(context);
        MethodCallerBytecodeGenerator classGenerator = createGenerator(target);
        classGenerator.setStrategy(weavingStrategy);
        classGenerator.setSource(context.getConfiguredCallSource());
        classGenerator.setUseCache(false);
        return classGenerator.create();
    }

    protected CglibWeavingGeneratorStrategy createWeavingStrategy(CallContext context) {
        return new CglibWeavingGeneratorStrategy(context.getConfiguredWeaver());
    }

    protected MethodCallerBytecodeGenerator createGenerator(CallTarget target) {
        return new MethodCallerBytecodeGenerator(target);
    }

}
