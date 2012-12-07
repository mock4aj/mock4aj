package info.rubico.mock4aj.api.exceptions;

/**
 * It's not possible to simulate a call coming from that type (final, ...)
 */
public class UnsupportedSourceType extends Mock4AjException {

    // @formatter:off
    private static final String MESSAGE = 
                              "It is not possible to use the type '%s' as a call source.\n"
                            + "The source type is probably a final class.";
    // @formatter:on

    private static final long serialVersionUID = 1L;

    public UnsupportedSourceType(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedSourceType(String message) {
        super(message);
    }

    public UnsupportedSourceType(Class<?> type) {
        super(String.format(MESSAGE, type));
    }

    public UnsupportedSourceType(Class<?> type, Throwable cause) {
        super(String.format(MESSAGE, type), cause);
    }
}
