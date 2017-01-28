package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.TimeMatrixEntry;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static de.tu_berlin.dima.niteout.routing.LocationDirectory.ALEXANDERPLATZ;
import static de.tu_berlin.dima.niteout.routing.LocationDirectory.BRANDENBURGER_TOR;

public class HereApiWrapperTest {

    private static Location[] B_TOR_ARRAY_MANY = (Location[]) fillWith(new Location[500], BRANDENBURGER_TOR);
    private static Location[] B_TOR_ARRAY_ONE = {BRANDENBURGER_TOR};

    private HereApiWrapper api;

    @Before
    public void init() {
        api = new HereApiWrapper(System.getProperty("API_HERE_APP_ID"), System.getProperty("API_HERE_APP_CODE"));
    }

    @Test
    public void getMatrixTest() {
        Location[] starts = new Location[30],
            destinations = new Location[20];
        for (int i = 0; i < starts.length; i++) {
            starts[i] = LocationDirectory.getRandomLocationInBerlin();
        }
        for (int i = 0; i < destinations.length; i++) {
            destinations[i] = LocationDirectory.getRandomLocationInBerlin();
        }
        List<TimeMatrixEntry> list =
                api.getMultiModalMatrix(starts, destinations, LocalDateTime.now());
        int i = 5;
    }

    private static Object[] fillWith(Object[] array, Object filler) {
        for (int i = 0; i < array.length; i++) {
            array[i] = filler;
        }
        return array;
    }
}
