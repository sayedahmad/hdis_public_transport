package de.tu_berlin.dima.niteout.routing;


import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Test class for {@link MapzenApiWrapper}.
 */
public class MapzenApiWrapperTest {

    private final String apiKey = System.getProperty("API_KEY_MAPZEN");


    @Test
    public void getWalkingTripTime() {

        MapzenApiWrapper fixture = new MapzenApiWrapper(this.apiKey);
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

        MapzenApiWrapper fixture = new MapzenApiWrapper(this.apiKey);
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
