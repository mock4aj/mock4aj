package info.rubico.mock4aj.examples.house;

@SuppressWarnings("unused")
public class Building {

    private final String shortDescription;
    private boolean energySaving;

    public Building(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void enter() {
    }

    public void leave() {
    }

    public void turnOnEnergySaving() {
        energySaving = true;
    }

    public void turnOffEnergySaving() {
        energySaving = false;
    }

    public boolean isInEnergySavingMode() {
        return energySaving;
    }
}
