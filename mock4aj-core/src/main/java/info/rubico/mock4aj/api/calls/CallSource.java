package info.rubico.mock4aj.api.calls;

public interface CallSource {

    /**
     * @return The type of the source class. Could be an interface or a concrete class.
     */
    Class<?> getType();

    /**
     * @return The name of the source class (including the package path).
     */
    String getName();

}
