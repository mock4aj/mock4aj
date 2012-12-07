package info.rubico.mock4aj.api.exceptions;

/**
 * It's not possible to simulate a call to that type (final, ...)
 */
public class UncallableType extends Mock4AjException {

    // @formatter:off
    private static final String MESSAGE = 
                              "It is not possible to simulate a call to type '%s'.\n"
                            + "The targetted type is probably a final class.";
    // @formatter:on

    private static final long serialVersionUID = 1L;

    public UncallableType(String message, Throwable cause) {
        super(message, cause);
    }

    public UncallableType(String message) {
        super(message);
    }

    public UncallableType(Class<?> type) {
        super(String.format(MESSAGE, type));
    }

    public UncallableType(Class<?> type, Throwable cause) {
        super(String.format(MESSAGE, type), cause);
    }
}
