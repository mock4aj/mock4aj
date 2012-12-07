package info.rubico.mock4aj.api.exceptions;

/**
 * It's not possible to create a proxy for that type (final, ...)
 */
public class UnproxiableType extends Mock4AjException {

    // @formatter:off
    private static final String MESSAGE = 
                              "It is not possible to create a proxy for the type '%s'.\n"
                            + "It is not possible to create a proxy to:\n"
                            + "   - a final class;\n"
                            + "   - an anonymous class;\n"
                            + "   - a primitive type.";
    // @formatter:on

    private static final long serialVersionUID = 1L;

    public UnproxiableType(String message, Throwable cause) {
        super(message, cause);
    }

    public UnproxiableType(String message) {
        super(message);
    }

    public UnproxiableType(Class<?> type) {
        super(String.format(MESSAGE, type));
    }

    public UnproxiableType(Class<?> type, Throwable cause) {
        super(String.format(MESSAGE, type), cause);
    }
}
