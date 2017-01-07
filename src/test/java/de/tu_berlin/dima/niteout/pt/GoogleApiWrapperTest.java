package de.tu_berlin.dima.niteout.pt;

import de.tu_berlin.dima.niteout.pt.data.Location;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Test class for the google wrapper
 */
public class GoogleApiWrapperTest {

    private Location ernstReuter;
    private Location hotetWaldorfAstoria;
    private LocalDate departureTime;

    @Before
    public void setup() {
        ernstReuter = new Location("52.512127", "13.322874");
        hotetWaldorfAstoria = new Location("52.505831", "13.333122");
        departureTime = LocalDate.now();
    }

    @Test
    public void getDirections() throws Exception {
        GoogleApiWrapper wrapper = new GoogleApiWrapper("AIzaSyBBA2_glIM1dpWFNZZkCAC_jWNj_FrUHsA");
        wrapper.getDirections(ernstReuter, hotetWaldorfAstoria, departureTime);
    }
}