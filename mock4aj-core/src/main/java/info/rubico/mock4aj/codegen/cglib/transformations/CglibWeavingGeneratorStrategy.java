package info.rubico.mock4aj.codegen.cglib.transformations;

import info.rubico.mock4aj.api.weaving.Weaver;
import net.sf.cglib.asm.ClassReader;
import net.sf.cglib.core.ClassNameReader;
import net.sf.cglib.core.DefaultGeneratorStrategy;

/**
 * A generator strategy for cglib that allow weaving of the newly generated class.
 * 
 * The generated but not yet loaded bytecode is transformed using a {@link Weaver}.
 */
public class CglibWeavingGeneratorStrategy extends DefaultGeneratorStrategy {

    private final Weaver weaver;

    public CglibWeavingGeneratorStrategy(Weaver weaver) {
        super();
        this.weaver = weaver;
    }

    @Override
    protected byte[] transform(byte[] bytecode) throws Exception { // NOPMD
        byte[] bytecodeToWeave = super.transform(bytecode);
        String className = ClassNameReader.getClassName(new ClassReader(bytecodeToWeave));
        return weaver.weaveClassBytes(className, bytecodeToWeave);
    }

    public Weaver getWeaver() {
        return weaver;
    }
}