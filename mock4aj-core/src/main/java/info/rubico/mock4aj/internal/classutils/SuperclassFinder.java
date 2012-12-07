package info.rubico.mock4aj.internal.classutils;

/**
 * <p>
 * Figures out which class should be the superclass of the generated class. It allows the generated
 * class to mimic a certain type. So we must find what class we extend (read which class we proxy).
 * <h2>Warning</h2>
 * It finds only classes to <strong>extend</strong> not interfaces to implement.
 * <h2>Motivations</h2>
 * In some situation (like with mocks) the object to mimic is not the real class type since it is
 * already a enhanced/generated class. Often the type of the generated class must be the nearest
 * real class. It is an example and this depends on the implementation of the
 * {@link SuperclassFinder}.
 * </p>
 */
public interface SuperclassFinder {

    Class<?> findSuperclass(final Object objectToProxy);

}
