package de.tu_berlin.dima.niteout.pt;


import de.tu_berlin.dima.niteout.pt.model.mapzen.CostingModel;
import org.junit.Test;
import de.tu_berlin.dima.niteout.pt.model.*;

import java.io.IOException;

/**
 * Test class for {@link MapzenApiWrapper}.
 */
public class MapzenApiWrapperTest {

    private final String apiKey = "mapzen-pj9Lo9N";
    private final Location TuLocation = new Location(13.32697, 52.51221);
    private final Location HbfLocation = new Location(13.369563, 52.524742);

    @Test
    public void getRouteResponse() {

        MapzenApiWrapper fixture = new MapzenApiWrapper(this.apiKey);
        try {
            String response = fixture.getRouteResponse(TuLocation, HbfLocation, CostingModel.PEDESTRIAN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
