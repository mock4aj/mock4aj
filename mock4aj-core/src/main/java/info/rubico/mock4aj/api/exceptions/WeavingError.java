package info.rubico.mock4aj.api.exceptions;

/**
 * An error occurs during the weaving process.
 */
public class WeavingError extends Mock4AjException {

    private static final long serialVersionUID = 1L;

    public WeavingError(final String message, final Throwable cause) {
        super(message, cause);
    }

}
