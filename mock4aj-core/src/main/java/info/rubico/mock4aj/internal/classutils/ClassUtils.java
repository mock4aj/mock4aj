package info.rubico.mock4aj.internal.classutils;

import java.lang.reflect.Modifier;

/**
 * Some utils about Java classes
 */
public final class ClassUtils {

    private ClassUtils() {
    }

    public static boolean isFinalClass(Class<?> clazz) {
        int modifier = clazz.getModifiers();
        return Modifier.isFinal(modifier);
    }

}
