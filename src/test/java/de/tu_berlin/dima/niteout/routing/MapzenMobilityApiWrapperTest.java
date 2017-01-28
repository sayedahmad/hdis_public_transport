package de.tu_berlin.dima.niteout.routing;


import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Test class for {@link MapzenMobilityApiWrapper}.
 */
public class MapzenMobilityApiWrapperTest {

    private final String apiKey = System.getProperty("API_KEY_MAPZEN");


    @Test
    public void getWalkingTripTime() {

        MapzenMobilityApiWrapper fixture = new MapzenMobilityApiWrapper(this.apiKey);
        int tripDuration = 0;

        try {
            tripDuration = fixture.getWalkingTripTime(LocationDirectory.TU_BERLIN, LocationDirectory.HAUPTBAHNHOF);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        Assert.assertNotEquals(tripDuration, -1);
    }

    @Test
    public void getPublicTransportTripTime() {

        MapzenMobilityApiWrapper fixture = new MapzenMobilityApiWrapper(this.apiKey);
        int tripDuration = 0;

        try {
            tripDuration = fixture.getPublicTransportTripTime(
                    LocationDirectory.TU_BERLIN,
                    LocationDirectory.HAUPTBAHNHOF,
                    LocalDateTime.now());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        Assert.assertNotEquals(tripDuration, -1);
    }
}
