package info.rubico.mock4aj.api.exceptions;

/**
 * The aspect is abstract but shouldn't.
 */
public class NotAConcreteAspect extends Mock4AjException {

    private static final long serialVersionUID = 1L;

    public NotAConcreteAspect(final String message) {
        super(message);
    }

    public NotAConcreteAspect(final Class<?> abstractAspect) {
        super(String.format("A concrete aspect was expected but the aspect '%s' is abstract.\n"
                            + "\n"
                            + "\tTypically this error happens when you tried to weave an"
                            + "abstract aspect. You must extend it first.", abstractAspect));
    }

}
