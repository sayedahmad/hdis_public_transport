package de.tu_berlin.dima.niteout.pt;

import de.tu_berlin.dima.niteout.pt.data.Location;
import de.tu_berlin.dima.niteout.pt.data.Segment;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the google wrapper
 */
public class GoogleApiWrapperTest {

    private Location ernstReuter;
    private Location hotelWaldorfAstoria;
    private LocalDateTime departureTime;

    @Before
    public void setup() {
        ernstReuter = new Location(52.512127, 13.322874);
        hotelWaldorfAstoria = new Location(52.505831, 13.333122);
        departureTime = LocalDateTime.now();
    }

    @Test
    public void getDirections() throws Exception {
        GoogleApiWrapper wrapper = new GoogleApiWrapper("AIzaSyBBA2_glIM1dpWFNZZkCAC_jWNj_FrUHsA");
        List<Segment> directions = wrapper.getDirections(ernstReuter, hotelWaldorfAstoria, departureTime);
        assertFalse(directions.isEmpty());
        // TODO more asserts like check eg: 1min < duration < 1 hour
    }
}