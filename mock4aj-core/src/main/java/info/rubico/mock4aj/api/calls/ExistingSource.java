package info.rubico.mock4aj.api.calls;

public class ExistingSource implements CallSource {

    private final Class<?> sourceClass;

    public ExistingSource(Class<?> sourceClass) {
        this.sourceClass = sourceClass;

    }

    public Class<?> getType() {
        return sourceClass;
    }

    public String getName() {
        return sourceClass.getName();
    }

}
