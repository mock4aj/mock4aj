package info.rubico.mock4aj.api.exceptions;

/**
 * Encapsulate an exception thrown by the target method. This is not an error with the
 * CallerGenerator but simply a legitimate exception that as been throw by the target.
 * <p>
 * The original exception throw by the target can be extracted by
 * {@link EncapsulatedExceptionThrownByTarget#getCause()}
 */
public class EncapsulatedExceptionThrownByTarget extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EncapsulatedExceptionThrownByTarget(String message, Throwable cause) {
        super(message, cause);
    }

    public EncapsulatedExceptionThrownByTarget(Throwable cause) {
        super(cause);
    }

}
