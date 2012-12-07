package info.rubico.mock4aj.api.exceptions;

/**
 * Base exception for Mock4AspectJ.
 * 
 * It's a Runtime Exception since Mock4AspectJ is used within tests.
 */
public class Mock4AjException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public Mock4AjException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public Mock4AjException(final String message) {
        super(message);
    }

}
