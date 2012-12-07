package info.rubico.mock4aj.weavers.aspectj;

import org.aspectj.bridge.IMessageHandler;
import org.aspectj.weaver.ICrossReferenceHandler;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ReferenceTypeDelegate;
import org.aspectj.weaver.loadtime.IWeavingContext;
import org.aspectj.weaver.ltw.LTWWorld;
import org.aspectj.weaver.reflect.ReflectionBasedReferenceTypeDelegateFactory;

/**
 * <p>
 * A {@link LTWWorld} that supports dynamically created classes such as proxies and mocks. The main
 * difference with the {@link LTWWorld} is that it if a type is not in the AspectJ world (unknown by
 * the weaver), it will <strong>try to find the type using Java Reflection</strong>.
 * </p>
 * 
 * <p>
 * Note: Because of the AspecTJ architecture some code had to be copied because it was not possible
 * at that time to reuse it without importing undesirable behaviours. For now, we don't want to have
 * to modify AspectJ and we want be as independent as possible from it to be able to easily support
 * future versions.
 * </p>
 */
public class DynamicRuntimeWorld extends LTWWorld {

    public DynamicRuntimeWorld(final ClassLoader loader,
                               final IWeavingContext weavingContext,
                               final IMessageHandler handler,
                               final ICrossReferenceHandler xrefHandler)
    {
        super(loader, weavingContext, handler, xrefHandler);
    }

    @Override
    protected ReferenceTypeDelegate resolveDelegate(ReferenceType ty) { // NOPMD
        ReferenceTypeDelegate bcelDelegate = super.resolveDelegate(ty);
        if (bcelDelegate != null) {
            return bcelDelegate;
        }
        return ReflectionBasedReferenceTypeDelegateFactory.createDelegate(ty,
                                                                          this,
                                                                          getClassLoader());
    }

}
