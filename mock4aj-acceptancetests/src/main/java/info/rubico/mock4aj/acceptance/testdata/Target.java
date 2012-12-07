package info.rubico.mock4aj.acceptance.testdata;

/**
 * A simple interface that aspects will target in tests.
 */
public interface Target {

    void theMethod();

    void otherMethod();

    int retInt();

    void throwingMethod() throws SomeException;

    void markWeaved(Class<?> fromAspect);
}
