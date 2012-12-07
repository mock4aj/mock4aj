package info.rubico.mock4aj.api.exceptions;

/**
 * A class object was passed to be proxied. An instance of the class must be used instead.
 */
public class NotAnInstanceToProxy extends Mock4AjException {

    // @formatter:off
    private static final String MESSAGE = 
                            "It is not possible to create directly a proxy of a class. You must "
                          + "use an instance of the class you want to proxy instead. The proxy "
                          + "factory will extract the class from the object.\n"
                          + "It's because you want to create a proxy that will forward calls "
                          + "to a specific mock instance that is already created. The mocking of "
                          + "the class should be already done by a mock creator.\n"
                          + "Example:\n"
                          + "    Type mock = mockUsingSomeFramework(Type.class);\n"
                          + "    Type proxy = createWeavedProxy(mock);";
    //@formatter:on

    private static final long serialVersionUID = 1L;

    public NotAnInstanceToProxy(String message) {
        super(message);
    }

    public NotAnInstanceToProxy() {
        super(MESSAGE);
    }

    public NotAnInstanceToProxy(Throwable cause) {
        super(MESSAGE, cause);
    }
}
