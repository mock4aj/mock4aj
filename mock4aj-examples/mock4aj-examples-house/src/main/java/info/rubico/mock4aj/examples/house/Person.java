package info.rubico.mock4aj.examples.house;

@SuppressWarnings("unused")
public class Person {

    private final Building home;
    private final Building office;
    private final Car car;
    private final String name;

    public Person(String name) {
        this.name = name;
        this.home = new Building(String.format("%s's Home.", name));
        this.office = new Building(String.format("%s's Office.", name));
        this.car = new Car();
    }

    public void goToWork() {
        home.leave();
        car.start();
        car.driveTo(office);
        car.stop();
        office.enter();
    }

    public void goHome() {
        office.leave();
        car.start();
        car.driveTo(home);
        car.stop();
        home.enter();
    }

}
