package de.tu_berlin.dima.niteout.pt;

import de.tu_berlin.dima.niteout.pt.data.Location;
import de.tu_berlin.dima.niteout.pt.data.Segment;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the google wrapper
 */
public class GoogleApiWrapperTest {

    private final static Location ALEX = new Location(52.521918, 13.412818);
    private final static Location BRANDENBURGER_TOR = new Location(52.516298, 13.378482);
    public final static LocalDateTime PUBLIC_TRANSPORT_TRIP_START_TIME = LocalDateTime.of(2017, Month.JANUARY, 19, 10, 10);



    @Test
    public void getDirections() throws Exception {
        GoogleApiWrapper wrapper = new GoogleApiWrapper("AIzaSyBBA2_glIM1dpWFNZZkCAC_jWNj_FrUHsA");
        List<Segment> directions = wrapper.getDirections(ALEX, BRANDENBURGER_TOR, PUBLIC_TRANSPORT_TRIP_START_TIME);
        assertFalse(directions.isEmpty());
        // TODO more asserts like check eg: 1min < duration < 1 hour
    }
}