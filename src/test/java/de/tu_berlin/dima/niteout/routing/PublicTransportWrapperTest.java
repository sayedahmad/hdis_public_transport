package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.*;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static de.tu_berlin.dima.niteout.routing.LocationDirectory.ALEXANDERPLATZ;
import static de.tu_berlin.dima.niteout.routing.LocationDirectory.BRANDENBURGER_TOR;
import static org.junit.Assert.*;

public class PublicTransportWrapperTest {

    private static BoundingBox BERLIN_MITTE = new BoundingBox(13.3295,52.4849, 13.4483, 52.5439);

    private PublicTransportWrapper api;

    @Before
    public void init() {
        try {
            api = new HereWrapper(System.getProperty("API_HERE_APP_ID"), System.getProperty("API_HERE_APP_CODE"));
        } catch (RoutingAPIException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void getTimeTest() {
        int time = 0;
        try {
            time = api.getPublicTransportTripTime(BRANDENBURGER_TOR, ALEXANDERPLATZ, LocalDateTime.now());
        } catch (RoutingAPIException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
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
        List<TimeMatrixEntry> list = null;
        try {
            list = api.getMultiModalMatrix(starts, destinations, LocalDateTime.now());
        } catch (RoutingAPIException e) {
            fail(e.getMessage());
        }
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    @Test
    public void getPublicTransportRouteSummaryTest() {
        // always next monday 12:37 to ensure there is traffic and the api call is in near future
        LocalDateTime time = LocalDateTime.now().withHour(12).withMinute(37).with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        RouteSummary routeSummary = null;
        try {
            routeSummary = api.getPublicTransportRouteSummary(BRANDENBURGER_TOR, ALEXANDERPLATZ,time);
        } catch (RoutingAPIException e) {
            fail(e.getMessage());
        }

        assertNotNull(routeSummary);
        // test aggregated travel times map == duration
        assertEquals(routeSummary.getTotalDuration(),
                routeSummary.getModeOfTransportTravelTimes().values().stream().mapToInt(Integer::intValue).sum());
        // test departure + duration == arrival
        assertEquals(routeSummary.getDepartureTime().plusSeconds(routeSummary.getTotalDuration()),
                routeSummary.getArrivalTime());
        // duration less than 2 hours
        assertTrue(routeSummary.getTotalDuration() < 7200);
        LocalDateTime dep = routeSummary.getDepartureTime(),
                arr = routeSummary.getArrivalTime();
        // requested time to scheduled time max 1 hour diff
        assertTrue(time.until(dep, ChronoUnit.HOURS) < 1);
        // from btor to alex max 4 changes
        assertTrue(routeSummary.getNumberOfChanges() < 5);

        // attributes not tested at all: distance
    }
}
