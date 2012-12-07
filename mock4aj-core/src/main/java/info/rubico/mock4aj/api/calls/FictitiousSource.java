package info.rubico.mock4aj.api.calls;

public class FictitiousSource implements CallSource {

    private static final Class<?> DEFAULT_TYPE = MethodCaller.class;

    private final String fictitiousClassName;
    private Class<?> parentType;

    public FictitiousSource(String fictitiousClassName) {
        this.fictitiousClassName = fictitiousClassName;
        this.parentType = DEFAULT_TYPE;
    }

    public Class<?> getType() {
        return parentType;
    }

    public String getName() {
        return fictitiousClassName;
    }

    public FictitiousSource extending(Class<?> parentClass) {
        parentType = parentClass;
        return this;
    }

    public FictitiousSource implementing(Class<?> parentInterface) {
        return extending(parentInterface);
    }

}
