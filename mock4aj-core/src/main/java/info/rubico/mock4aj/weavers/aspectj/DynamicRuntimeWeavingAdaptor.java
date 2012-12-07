/*******************************************************************************
 * Copyright (c) 2005 Contributors. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution and is available at http://eclipse.org/legal/epl-v10.html
 * 
 * Based on ClassLoaderWeavingAdaptor from AspectJ project.
 * 
 * Contributors: Felix-Antoine Bourbonnais
 *******************************************************************************/
package info.rubico.mock4aj.weavers.aspectj;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.util.LangUtil;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.bcel.BcelWeakClassLoaderReference;
import org.aspectj.weaver.bcel.BcelWeaver;
import org.aspectj.weaver.loadtime.DefaultWeavingContext;
import org.aspectj.weaver.loadtime.IWeavingContext;
import org.aspectj.weaver.tools.GeneratedClassHandler;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;
import org.aspectj.weaver.tools.WeavingAdaptor;

/**
 * <p>
 * Adaptor used to weave into dynamic objects (like proxies and mocks) that are created at runtime.
 * </p>
 * 
 * <p>
 * Unlike normal LTW Weaving adapters from AspectJ, <strong>aspects can be registered and
 * unregistered at any time.</strong>
 * </p>
 * 
 * <p>
 * The adaptor is not intended to be used as a LTW adaptor with a Java Agent or the AspectJ
 * ClassLoader. It is designed to be used at runtime with pure java code.
 * </p>
 * 
 * <p>
 * Objects are not automatically weaved since it is designed to weave at runtime and not at
 * loadtime. Object are weaved by calling the {@code weaveClass} method.
 * </p>
 * 
 * <p>
 * AspectJ Weaver configuration files are not read (like aop.xml) since it is intended to be used
 * and configuration at runtime with Pure Java.
 * </p>
 * 
 * <p>
 * Note: Because of the AspecTJ architecture some code had to be copied because it was not possible
 * at that time to reuse it without importing undesirable behaviours. For now, we don't want to have
 * to modify AspectJ and we want be as independent as possible from it to be able to easily support
 * future versions.
 * </p>
 */
@SuppressWarnings("rawtypes")
public class DynamicRuntimeWeavingAdaptor extends WeavingAdaptor { // NOPMD

    private static final String NOT_INIT_CONTEXT_ID = "NOT INITIALIZED CONTEXT";

    private boolean initialized;

    private IWeavingContext weavingContext;

    private static Trace trace = TraceFactory.getTraceFactory()
        .getTrace(DynamicRuntimeWeavingAdaptor.class);

    private final ClassLoader classLoader;

    public DynamicRuntimeWeavingAdaptor(final ClassLoader classLoader) {
        super();

        this.classLoader = classLoader;

        if (trace.isTraceEnabled()) {
            trace.enter("<init> MockWeavingAdaptor", this);
        }
        if (trace.isTraceEnabled()) {
            trace.exit("<init> MockWeavingAdaptor");
        }
    }

    public synchronized void initialize() {
        if (initialized) {
            return;
        }

        initMessageHandler();
        weavingContext = new DefaultWeavingContext(classLoader);
        generatedClassHandler = new SimpleGeneratedClassHandler(classLoader);
        initWorld();
        initWeaver();

        enable();
        initialized = true;

        if (trace.isTraceEnabled()) {
            trace.exit("initialize", isEnabled());
        }
    }

    protected void initMessageHandler() {
        super.createMessageHandler();

        IMessageHandler messageHandler = getMessageHandler();
        // TODO Configure verbosity
        messageHandler.dontIgnore(IMessage.INFO); // VERBOSE
        messageHandler.dontIgnore(IMessage.DEBUG);
        messageHandler.dontIgnore(IMessage.WEAVEINFO);
    }

    protected void initWorld() {
        bcelWorld = new DynamicRuntimeWorld(classLoader, weavingContext,
                                            getMessageHandler(), null);

        bcelWorld.setMessageHandler(getMessageHandler());
        bcelWorld.setTiming(false, true);
        bcelWorld.setBehaveInJava5Way(LangUtil.is15VMOrGreater());

        // TODO Configure LINT infos
        bcelWorld.getLint().setAll("ignore"); // DISABLE LINT
    }

    protected void initWeaver() {
        weaver = new BcelWeaver(bcelWorld);

        // Yes... true = *not*!
        final boolean notReweavable = true; // NOPMD
        weaver.setReweavableMode(notReweavable);

        weaver.prepareForWeave();
    }

    /**
     * When a class is generated (weaved), this simple handler will simply define the class in the
     * Class Loader.
     * 
     * <p>
     * <em>ADAPTED from {@link org.aspectj.weaver.loadtime.ClassLoaderWeavingAdaptor} </em>. We
     * don't want the complete behaviour and the method is not reusable.
     * </p>
     */
    class SimpleGeneratedClassHandler implements GeneratedClassHandler {
        private final BcelWeakClassLoaderReference loaderRef;

        SimpleGeneratedClassHandler(final ClassLoader loader) {
            loaderRef = new BcelWeakClassLoaderReference(loader);
        }

        public void acceptClass(final String name, final byte[] bytes) {
            defineClass(loaderRef.getClassLoader(), name, bytes);
        }
    }

    // CHECKSTYLE:OFF -----

    /**
     * Define/Load the class into the ClassLoader.
     * 
     * <p>
     * <em>COPIED from {@link org.aspectj.weaver.loadtime.ClassLoaderWeavingAdaptor}.</em> We don't
     * want the complete behaviour and unfortunately, the method is not reusable.
     * </p>
     */
    private void defineClass(final ClassLoader loader, final String name, final byte[] bytes) {

        if (trace.isTraceEnabled()) {
            trace.enter("defineClass", this, new Object[] { loader, name, bytes });
        }
        debug("generating class '" + name + "'");

        Object clazz = null;
        try {
            Class[] defineClassParams = { String.class, bytes.getClass(), int.class, int.class };
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass",
                                                                     defineClassParams);
            defineClass.setAccessible(true);
            clazz = defineClass.invoke(loader, new Object[] { name, bytes,
                                                             Integer.valueOf(0),
                                                             Integer.valueOf(bytes.length) });
        }
        catch (InvocationTargetException e) {
            warn("define generated class failed", e.getTargetException());
        }
        catch (Exception e) {
            warn("define generated class failed", e);
        }

        if (trace.isTraceEnabled()) {
            trace.exit("defineClass", clazz);
        }
    }

    // CHECKSTYLE:ON -----

    @Override
    public String getContextId() {
        if (weavingContext != null) {
            return weavingContext.getId();
        }
        return NOT_INIT_CONTEXT_ID;
    }

    public synchronized boolean isClassInGeneratedCache(final String className) {
        return generatedClasses.containsKey(className);
    }

    public synchronized void flushGeneratedClassesCache() {
        generatedClasses = new HashMap();
    }

    public synchronized void registerAspect(final String aspectClassName) {
        info("register aspect " + aspectClassName);
        weaver.addLibraryAspect(aspectClassName);
        weaver.prepareForWeave();
    }

    public synchronized void unregisterAspect(final String aspectClassName) {
        info("unregister aspect " + aspectClassName);
        weaver.deleteClassFile(aspectClassName);
        weaver.prepareForWeave();
    }

    public boolean isAspect(final Class<?> clazz) {
        ResolvedType type = bcelWorld.resolve(clazz.getName());
        return type.isAspect();
    }

    public synchronized void reset() {
        info("reset wevaing adapter");
        flushGeneratedClassesCache();
        initialized = false;
        initialize();
    }
}