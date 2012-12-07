package info.rubico.mock4aj.api.exceptions;

/**
 * The call's target is not valid.
 */
public class InvalidCallTarget extends Mock4AjException {
    // @formatter:off
    private static final String MESSAGE = 
                            "The call's target is not a valid target. There is two possbilities:\n"
                            + "  a) the target object is not usable as a target;\n"
                            + "  b) the target method exists but can't be called "
                            + "     (not public, ...).";
    //@formatter:on

    private static final long serialVersionUID = 1L;

    public InvalidCallTarget(String message) {
        super(message);
    }

    public InvalidCallTarget() {
        super(MESSAGE);
    }

    public InvalidCallTarget(Throwable cause) {
        super(MESSAGE, cause);
    }
}
