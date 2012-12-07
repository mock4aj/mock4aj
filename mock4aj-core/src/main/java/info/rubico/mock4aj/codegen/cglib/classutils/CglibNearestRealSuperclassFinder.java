package info.rubico.mock4aj.codegen.cglib.classutils;

import info.rubico.mock4aj.internal.classutils.SuperclassFinder;
import net.sf.cglib.proxy.Enhancer;

/**
 * <p>
 * Cglib doesn't support another dynamic or synthetic class as the superclass so we try to take the
 * first "real class" (not generated) in the hierarchy by eliminating drastically everything that
 * seems to be an enhanced, synthetic or not a real class.
 * </p>
 */
public class CglibNearestRealSuperclassFinder implements SuperclassFinder {

    public static final SuperclassFinder INSTANCE = new CglibNearestRealSuperclassFinder();

    public Class<?> findSuperclass(final Object objectToProxy) {
        Class<?> potentialSuperclass = objectToProxy.getClass();
        while (isSyntheticOrGeneratedClass(potentialSuperclass)) {
            potentialSuperclass = potentialSuperclass.getSuperclass();
        }
        return potentialSuperclass;
    }

    /**
     * Try to determined of the given class is a synthetic/dynamic class or a "real" class. To do
     * so, we try:
     * <ul>
     * <li>using the Java's reflection isSynthetic method;
     * <li>using the Cglib's isEnhanced method (if it is a std. cglib enhanced method);
     * <li>finally, we consider every class's name containing a "$$" as a synthetic method.
     * </ul>
     * 
     * @return true if it seems to be a synthetic/dynamic class, else if not.
     */
    protected boolean isSyntheticOrGeneratedClass(final Class<?> clazz) {
        if (clazz.isSynthetic() || Enhancer.isEnhanced(clazz)) {
            return true;
        }
        return clazz.getName().contains("$$");
    }

}
