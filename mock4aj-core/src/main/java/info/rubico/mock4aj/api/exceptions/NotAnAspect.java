package info.rubico.mock4aj.api.exceptions;

/**
 * An aspect was expected but a non-aspect class was given.
 */
public class NotAnAspect extends Mock4AjException {

    private static final long serialVersionUID = 1L;

    public NotAnAspect(final String message) {
        super(message);
    }

    public NotAnAspect(final Class<?> nonAspectClass) {
        super(String.format("An aspect was expected but the class '%s' is not.", nonAspectClass));
    }

}
