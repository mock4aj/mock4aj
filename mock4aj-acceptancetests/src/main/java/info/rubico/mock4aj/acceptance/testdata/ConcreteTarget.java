package info.rubico.mock4aj.acceptance.testdata;

/**
 * A simple concrete implementation of {@link Target} the will be use within tests.
 */
public class ConcreteTarget implements Target {

    public void theMethod() {
    }

    public void otherMethod() {

    }

    public void markWeaved(final Class<?> fromAspect) {
    }

    public int retInt() {
        return 0;
    }

    public void throwingMethod() throws SomeException {
        throw new SomeException("ConcreteTarget is throwing this");
    }

}
