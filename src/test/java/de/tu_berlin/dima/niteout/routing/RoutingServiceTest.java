package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.RouteSummary;
import de.tu_berlin.dima.niteout.routing.model.TransportMode;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for {@link RoutingService}.
 */
public class RoutingServiceTest {

    @Test
    public void getEmptyRoute() throws Exception {
//        RoutingService service = new RoutingService();
//        assertNotNull(service.getEmptyRoute());
//        assertEquals("No route found", service.getEmptyRoute());
    }

    @Test
    public void testGetWalkingTripTime() {
        RoutingService fixture = new RoutingService();
        int tripTime = 0;
        try {
            tripTime = fixture.getTripTime(TransportMode.WALKING,
                    LocationDirectory.TU_BERLIN, LocationDirectory.SIEGESSAEULE,
                    LocalDateTime.now());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(tripTime > 0);
    }

    @Test
    public void testGetWalkingRouteSummary() {

        RoutingService fixture = new RoutingService();
        LocalDateTime now = LocalDateTime.now();

        try {
            RouteSummary routeSummary = fixture.getRouteSummary(
                    TransportMode.WALKING,
                    LocationDirectory.TU_BERLIN, LocationDirectory.POTSDAMER_PLATZ,
                    LocalDateTime.now());
            Assert.assertNotNull(routeSummary);
            Assert.assertNotEquals(0, routeSummary.getTotalDuration());
            Assert.assertNotEquals(0, routeSummary.getTotalDistance());
            Assert.assertTrue(MINUTES.between(routeSummary.getDepartureTime(), now) <= 1);
            Assert.assertNotNull(routeSummary.getArrivalTime());
            Assert.assertTrue(routeSummary.getArrivalTime().isAfter(now));
            Assert.assertTrue(MINUTES.between(
                    now.plusSeconds(routeSummary.getTotalDuration()), routeSummary.getArrivalTime()) <= 1);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}