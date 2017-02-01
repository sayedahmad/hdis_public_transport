package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.*;
import de.tu_berlin.dima.niteout.routing.model.mapzen.Units;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static de.tu_berlin.dima.niteout.routing.LocationDirectory.ALEXANDERPLATZ;
import static de.tu_berlin.dima.niteout.routing.LocationDirectory.BRANDENBURGER_TOR;
import static org.junit.Assert.*;

public class PublicTranportAPIWrapperTest {

    private static BoundingBox BERLIN_MITTE = new BoundingBox(13.3295,52.4849, 13.4483, 52.5439);

    private PublicTranportAPI api;

    @Before
    public void init() {
        api = new HereApiWrapper(System.getProperty("API_HERE_APP_ID"), System.getProperty("API_HERE_APP_CODE"));
    }

    @Test
    public void getTimeTestFromBtorToAlex() {
        int time = api.getPublicTransportTripTime(BRANDENBURGER_TOR, ALEXANDERPLATZ, LocalDateTime.now());
        assertTrue(time > 300);
        assertTrue(time < 7200);
    }

    @Test
    public void getMatrixTestFromAndToRandomLocationsInBerlin() {
        Location[] starts = new Location[5],
            destinations = new Location[10];
        for (int i = 0; i < starts.length; i++) {
            starts[i] = LocationDirectory.getRandomLocation(BERLIN_MITTE);
        }
        for (int i = 0; i < destinations.length; i++) {
            destinations[i] = LocationDirectory.getRandomLocation(BERLIN_MITTE);
        }
        boolean[] indexed = new boolean[starts.length * destinations.length];
        long start = System.currentTimeMillis();
        List<TimeMatrixEntry> list = api.getMultiModalMatrix(starts, destinations, LocalDateTime.now());
        System.out.print("duration 50 calls: " + (System.currentTimeMillis() - start) + "ms");
        assertNotNull(list);
        assertEquals(list.size(), 50);
        list.forEach((TimeMatrixEntry e) -> {
            assertTrue(e.getTime() > 0);
            assertTrue(e.getTime() < 14400);
            assertTrue(e.getDistance() < (e.getUnits().equals(DistanceUnits.KILOMETERS) ? 200000 : 130000));
            assertTrue(e.getFromIndex() < starts.length);
            assertTrue(e.getFromIndex() >= 0);
            assertTrue(e.getToIndex() < destinations.length);
            assertTrue(e.getToIndex() >= 0);
            indexed[e.getFromIndex() + e.getToIndex()*starts.length] = true;
        });

        // none should be false, which they are if they were not in the returned list
        assertFalse(IntStream.range(0, starts.length * destinations.length).anyMatch(i -> !indexed[i]));

    }

    @Test
    public void getPublicTransportRouteSummaryTestFromBTorToAlexNextMondayTwelveThirtyseven() {
        // always next monday 12:37 to ensure there is traffic at the departure time
        LocalDateTime time = LocalDateTime.now().withHour(12).withMinute(37).with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        RouteSummary routeSummary = api.getPublicTransportRouteSummary(BRANDENBURGER_TOR, ALEXANDERPLATZ,time);

        assertNotNull(routeSummary);
        // test aggregated travel times map == duration
        assertEquals(routeSummary.getTotalDuration(),
                routeSummary.getModeOfTransportTravelTimes().values().stream().mapToInt(Integer::intValue).sum());
        // test departure + duration == arrival
        assertEquals(routeSummary.getDepartureTime().plusSeconds(routeSummary.getTotalDuration()),
                routeSummary.getArrivalTime());
        // duration less than 2 hours
        assertTrue(routeSummary.getTotalDuration() < 7200);
        // requested time to scheduled time max 1 hour diff
        assertTrue(time.until(routeSummary.getDepartureTime(), ChronoUnit.HOURS) < 1);
        // from btor to alex max 4 changes
        assertTrue(routeSummary.getNumberOfChanges() < 5);

        // from btor to alex around 3km
        assertTrue(routeSummary.getTotalDistance() > 1500 && routeSummary.getTotalDistance() < 5000);
    }
}
