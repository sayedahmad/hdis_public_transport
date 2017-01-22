package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Test class for {@link MapzenMatrixApiWrapper}
 * @author Andres Ardila
 */
public class MapzenMatrixApiWrapperTest {

    private final String apiKey = "mapzen-pj9Lo9N";

    @Test
    public void testGetWalkingMatrix() {

        MapzenMatrixApiWrapper fixture = new MapzenMatrixApiWrapper(this.apiKey);

        Location start = LocationDirectory.TU_BERLIN;
        Location[] destinations =
        {
            LocationDirectory.POTSDAMER_PLATZ,
            LocationDirectory.BRANDENBURGER_TOR,
            LocationDirectory.SIEGESSÄULE,
            LocationDirectory.getRandomLocationInBerlin()
        };

        try {
            fixture.getWalkingMatrix(start, destinations);

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
