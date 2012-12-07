package info.rubico.mock4aj.examples.house;

import static info.rubico.mock4aj.Mock4AspectJ.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

public class HouseAutomationTest {

    @Test
    public void shouldTurnEnergySavingOnAfterLeaving() {
        Building houseMock = mock(Building.class);
        Building houseWeavedMock = createWeavedProxy(houseMock, HouseAutomation.class);

        houseWeavedMock.leave();

        verify(houseMock).turnOnEnergySaving();
    }
}
