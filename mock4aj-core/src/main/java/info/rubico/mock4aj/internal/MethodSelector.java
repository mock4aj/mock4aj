package info.rubico.mock4aj.internal;

/**
 * Indicate that a class is used as a method selector. The class simulate the real class type but
 * don't do the real job. When the method is executed, it means that this <strong>method is
 * selected</strong>.
 * <p>
 * Those kind of class are dynamically generated. The class' type is the one in which we want to
 * select the method. For example, to select a method in {@link java.util.Date}, the
 * {@link MethodSelector} will be a generated class of the type {@link java.util.Date}. But calling
 * a method on it will select this method instead of doing the real {@link java.util.Date} behavior.
 * <p>
 * This is only a marker interface to enforce and indicate the role. The goal of the class
 * implementing this interface is to select a method and not to do the real method's job.
 */
public interface MethodSelector {

}
