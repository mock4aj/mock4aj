package info.rubico.mock4aj.codegen.cglib.calls.caller;

import info.rubico.mock4aj.api.calls.CallSource;
import info.rubico.mock4aj.api.calls.CallTarget;
import info.rubico.mock4aj.api.calls.ExistingSource;
import info.rubico.mock4aj.api.calls.MethodCaller;
import info.rubico.mock4aj.api.exceptions.EncapsulatedExceptionThrownByTarget;
import info.rubico.mock4aj.api.exceptions.Mock4AjException;
import info.rubico.mock4aj.api.exceptions.UnsupportedSourceType;
import info.rubico.mock4aj.internal.classutils.ClassUtils;

import java.lang.reflect.Method;

import net.sf.cglib.asm.ClassVisitor;
import net.sf.cglib.asm.Type;
import net.sf.cglib.core.AbstractClassGenerator;
import net.sf.cglib.core.Block;
import net.sf.cglib.core.ClassEmitter;
import net.sf.cglib.core.CodeEmitter;
import net.sf.cglib.core.Constants;
import net.sf.cglib.core.EmitUtils;
import net.sf.cglib.core.KeyFactory;
import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.Signature;
import net.sf.cglib.core.TypeUtils;

import org.objenesis.ObjenesisStd;

/**
 * Bytecode generator to create an on-demand class implementing {@link MethodCaller}.The generator
 * will generate a class that will simulate a call from a specific class without altering it.
 * <p>
 * A new class faking the source class will be dynamically created. This generated class will have a
 * {@link MethodCaller#doCall(Object)} method that will do a call to the target method on the object
 * given as argument.
 * 
 * <h2>Debugging</h2>
 * To dump all generated classes into .class files set the property
 * {@link net.sf.cglib.core.DebuggingClassWriter#DEBUG_LOCATION_PROPERTY} to a path.
 * <p>
 * Example:
 * 
 * <pre>
 * System.setProperty(net.sf.cglib.core.DebuggingClassWriter.DEBUG_LOCATION_PROPERTY,
 *                    "target/dump");
 * </pre>
 */
public class MethodCallerBytecodeGenerator extends AbstractClassGenerator { // NOPMD

    //@formatter:off

    private static final Source CGLIB_SOURCE = new Source(MethodCallerBytecodeGenerator.class.getName());
    
    private static final Class<?> OBJECT_CLASS = Object.class;
    private static final Class<?> METHODCALLER_CLASS = MethodCaller.class;

    /**
     * @See {@link KeyFactory}
     */
    interface CallerKey {
        Object newInstance(Method targetMethod, Class<?> fromClass);
    }

    private static final CallerKey KEY_FACTORY = (CallerKey) KeyFactory.create(CallerKey.class);



    private static final String TARGET_FIELD_NAME = "target";
    private static final String TARGET_SETTER_NAME = "Mock4Aj$setTarget";

    private static final Signature DOCALL_SIGNATURE = 
                            TypeUtils.parseSignature("Object doCall(Object[])");
    
    //@formatter:on

    private final CallTarget callTarget;
    private CallSource callSource;

    public MethodCallerBytecodeGenerator(CallTarget target) {
        super(CGLIB_SOURCE);

        this.callSource = new ExistingSource(METHODCALLER_CLASS);
        setNamePrefix(callSource.getName());

        this.callTarget = target;
        setNamingPolicy(new MethodCallerNamingPolicy(target));
    }

    public MethodCaller create() {
        Object key = KEY_FACTORY.newInstance(callTarget.getTargetMethod(), null);
        try {
            return (MethodCaller) super.create(key);
        }
        catch (Mock4AjException error) { // NOPMD
            throw error;
        }
        catch (Exception error) {
            throw new Mock4AjException("An error occurs during the creation of the Caller "
                                       + "class. If you can't find the problem by looking to the "
                                       + "cause, please contact the mailing list. ", error);
        }
    }

    public void generateClass(ClassVisitor visitor) {
        createCallerClass(visitor);
    }

    private void createCallerClass(ClassVisitor visitor) {
        Class<?> superclass = determineSuperclass(callSource.getType());
        Class<?>[] interfaces = determineInterfaces(callSource.getType());

        ClassEmitter classEmitter = new ClassEmitter(visitor);
        classEmitter.begin_class(Constants.V1_2,
                                 Constants.ACC_PUBLIC,
                                 getClassName(),
                                 Type.getType(superclass),
                                 TypeUtils.getTypes(interfaces),
                                 Constants.SOURCE_FILE);

        createTargetField(classEmitter);
        EmitUtils.null_constructor(classEmitter);
        createTargetSetter(classEmitter);
        createMethodDoCall(classEmitter);

        classEmitter.end_class();
    }

    protected Class<?>[] determineInterfaces(Class<?> callSourceClass) {
        Class<?>[] interfaces = new Class<?>[] { METHODCALLER_CLASS };
        if (callSourceClass.isInterface() && !callSourceClass.equals(METHODCALLER_CLASS)) {
            interfaces = new Class<?>[] { callSource.getType(), METHODCALLER_CLASS };
        }
        return interfaces;
    }

    protected Class<?> determineSuperclass(Class<?> callSourceClass) {
        Class<?> superclass = OBJECT_CLASS;
        if (!callSourceClass.isInterface()) {
            superclass = callSource.getType();
        }
        return superclass;
    }

    protected void createTargetField(ClassEmitter classEmitter) {
        classEmitter.declare_field(Constants.ACC_PUBLIC,
                                   TARGET_FIELD_NAME,
                                   Type.getType(OBJECT_CLASS),
                                   null);
    }

    protected void createTargetSetter(ClassEmitter classEmitter) {
        CodeEmitter codeEmitter = classEmitter
            .begin_method(Constants.ACC_PUBLIC,
                          new Signature(TARGET_SETTER_NAME,
                                        Type.VOID_TYPE,
                                        new Type[] { Type.getType(OBJECT_CLASS) }),
                          null);
        codeEmitter.load_this();
        codeEmitter.load_arg(0);
        codeEmitter.putfield(TARGET_FIELD_NAME);
        codeEmitter.return_value();
        codeEmitter.end_method();
    }

    protected void createMethodDoCall(ClassEmitter classEmitter) {
        Type returnType = Type.getType(callTarget.getTargetMethod().getReturnType());

        CodeEmitter codeEmitter = classEmitter.begin_method(Constants.ACC_PUBLIC,
                                                            DOCALL_SIGNATURE,
                                                            null);

        emitLoadTargetObject(codeEmitter);
        Block loadArgsBlock = emitLoadTargetArgs(codeEmitter);
        Block invokeBlock = emitInvokeTarget(codeEmitter);
        emitReturnTargetAnswer(returnType, codeEmitter);
        emitArgsExceptionsTable(codeEmitter, loadArgsBlock);
        emitInvokeExceptionsTable(invokeBlock);

        codeEmitter.end_method();
    }

    protected void emitLoadTargetObject(CodeEmitter codeEmitter) {
        codeEmitter.load_this();
        codeEmitter.getfield(TARGET_FIELD_NAME);
        codeEmitter.checkcast(Type.getType(callTarget.getTargetClass()));
    }

    protected Block emitLoadTargetArgs(CodeEmitter codeEmitter) {
        Type[] argsType = TypeUtils.getTypes(callTarget.getArgumentTypes());
        Block loadArgsBlock = codeEmitter.begin_block();
        for (int i = 0; i < argsType.length; i++) {
            codeEmitter.load_arg(0);
            codeEmitter.aaload(i);
            codeEmitter.unbox(argsType[i]);
        }
        loadArgsBlock.end();
        return loadArgsBlock;
    }

    protected void emitArgsExceptionsTable(CodeEmitter codeEmitter, Block loadArgsBlock) {
        Type[] argsType = TypeUtils.getTypes(callTarget.getArgumentTypes());
        if (argsType.length > 0) {
            codeEmitter.catch_exception(loadArgsBlock, Type
                .getType(ArrayIndexOutOfBoundsException.class));
            codeEmitter.throw_exception(Type.getType(IllegalArgumentException.class),
                                        "The number of arguments passed to doCall(...) doesn't "
                                                        + "match the target method signature.");

            EmitUtils.wrap_throwable(loadArgsBlock, Type.getType(IllegalArgumentException.class));
        }
    }

    protected Block emitInvokeTarget(CodeEmitter codeEmitter) {
        Block invokeBlock = codeEmitter.begin_block();
        codeEmitter.invoke(ReflectUtils.getMethodInfo(callTarget.getTargetMethod()));
        invokeBlock.end();
        return invokeBlock;
    }

    protected void emitInvokeExceptionsTable(Block invokeBlock) {
        EmitUtils.wrap_throwable(invokeBlock, Type
            .getType(EncapsulatedExceptionThrownByTarget.class));
    }

    protected void emitReturnTargetAnswer(Type returnType, CodeEmitter codeEmitter) {
        codeEmitter.box(returnType);
        codeEmitter.return_value();
    }

    @Override
    protected ClassLoader getDefaultClassLoader() {
        return METHODCALLER_CLASS.getClassLoader(); // NOPMD
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected Object firstInstance(Class type) {
        Object inst = new ObjenesisStd().newInstance(type);
        setTargetOnTheCreatedInstance(inst);
        return inst;
    }

    protected void setTargetOnTheCreatedInstance(Object inst) {
        try {
            Method setter = inst.getClass().getMethod(TARGET_SETTER_NAME,
                                                      new Class[] { Object.class });
            setter.invoke(inst, callTarget.getTargetInstance());
        }
        catch (Exception error) {
            throw new Mock4AjException("Unable to set the target instance on the created "
                                       + "caller. This problem should never happen since "
                                       + "this method is created just before!\n"
                                       + "\n"
                                       + "Please report this error to the mailing list.", error);
        }
    }

    @Override
    protected Object nextInstance(Object instance) {
        return instance;
    }

    public void setSource(CallSource callSource) {
        if (ClassUtils.isFinalClass(callSource.getType())) {
            throw new UnsupportedSourceType(callSource.getType());
        }

        this.callSource = callSource;
        setNamePrefix(callSource.getName());
    }

    protected CallSource getSource() {
        return callSource;
    }

}
