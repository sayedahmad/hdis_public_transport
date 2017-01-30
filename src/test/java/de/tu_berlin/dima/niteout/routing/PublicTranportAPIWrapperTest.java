package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.*;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static de.tu_berlin.dima.niteout.routing.LocationDirectory.ALEXANDERPLATZ;
import static de.tu_berlin.dima.niteout.routing.LocationDirectory.BRANDENBURGER_TOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PublicTranportAPIWrapperTest {

    private static Location[] B_TOR_ARRAY_MANY = (Location[]) fillWith(new Location[500], BRANDENBURGER_TOR);
    private static Location[] B_TOR_ARRAY_ONE = {BRANDENBURGER_TOR};
    private static BoundingBox BERLIN_MITTE = new BoundingBox(13.3295,52.4849, 13.4483, 52.5439);

    private PublicTranportAPI api;

    @Before
    public void init() {
        api = new HereApiWrapper(System.getProperty("API_HERE_APP_ID"), System.getProperty("API_HERE_APP_CODE"));
    }

    @Test
    public void getTimeTest() {
        int time = api.getPublicTransportTripTime(BRANDENBURGER_TOR, ALEXANDERPLATZ, LocalDateTime.now());
        assertTrue(time > 0);
    }

    @Test
    public void getMatrixTest() {
        Location[] starts = new Location[5],
            destinations = new Location[10];
        for (int i = 0; i < starts.length; i++) {
            starts[i] = LocationDirectory.getRandomLocation(BERLIN_MITTE);
        }
        for (int i = 0; i < destinations.length; i++) {
            destinations[i] = LocationDirectory.getRandomLocation(BERLIN_MITTE);
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

    @Test
    public void getPublicTransportRouteSummaryTest() {
        // always next monday 12:37 to ensure there is traffic and the api call is in near future
        LocalDateTime time = LocalDateTime.now().withHour(12).withMinute(37).with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        RouteSummary routeSummary = api.getPublicTransportRouteSummary(BRANDENBURGER_TOR, ALEXANDERPLATZ,time);

        assertNotNull(routeSummary);
        // test aggregated travel times map == duration
        assertEquals(routeSummary.getTotalDuration(),
                routeSummary.getModeOfTransportTravelTimes().values().stream().mapToInt(Integer::intValue).sum());
        // test departure + total travel time == arrival
        assertEquals(routeSummary.getDepartureTime().plusSeconds(routeSummary.getTotalDuration()),
                routeSummary.getArrivalTime());
        // from btor to alex max 4 changes
        assertTrue(routeSummary.getNumberOfChanges() < 5);
    }
}
