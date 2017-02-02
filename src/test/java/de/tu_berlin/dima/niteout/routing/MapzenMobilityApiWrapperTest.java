package de.tu_berlin.dima.niteout.routing;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Test class for {@link MapzenMobilityApiWrapper}.
 */
public class MapzenMobilityApiWrapperTest {

    private final String apiKey = System.getProperty("API_KEY_MAPZEN");
    private MapzenMobilityApiWrapper fixture;

    @Before
    public void setUpMapzenWrapper() {
        try {
            fixture = new MapzenMobilityApiWrapper(this.apiKey);
        } catch (RoutingAPIException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getWalkingTripTime() {

        int tripDuration = 0;

        try {
            tripDuration = fixture.getWalkingTripTime(LocationDirectory.TU_BERLIN, LocationDirectory.HAUPTBAHNHOF);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        Assert.assertNotEquals(tripDuration, -1);
    }

    @Test
    public void getPublicTransportTripTime() {

        int tripDuration = 0;

        try {
            tripDuration = fixture.getPublicTransportTripTime(
                    LocationDirectory.TU_BERLIN,
                    LocationDirectory.HAUPTBAHNHOF,
                    LocalDateTime.now());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        Assert.assertNotEquals(tripDuration, -1);
    }
}
