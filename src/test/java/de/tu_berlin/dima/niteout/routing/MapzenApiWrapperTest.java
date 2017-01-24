package de.tu_berlin.dima.niteout.routing;


import de.tu_berlin.dima.niteout.routing.MapzenApiWrapper;
import de.tu_berlin.dima.niteout.routing.model.Location;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Test class for {@link MapzenApiWrapper}.
 */
public class MapzenApiWrapperTest {

    private final String apiKey = "mapzen-pj9Lo9N";
    private final Location TuLocation = new Location(13.32697, 52.51221);
    private final Location HbfLocation = new Location(13.369563, 52.524742);

    @Test
    public void getWalkingTripTime() {

        MapzenApiWrapper fixture = new MapzenApiWrapper(this.apiKey);
        int tripDuration = 0;

        try {
            tripDuration = fixture.getWalkingTripTime(TuLocation, HbfLocation);
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
            tripDuration = fixture.getPublicTransportTripTime(TuLocation, HbfLocation, LocalDateTime.now());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        Assert.assertNotEquals(tripDuration, -1);
    }
}
