package de.tu_berlin.dima.niteout.pt;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import de.tu_berlin.dima.niteout.pt.data.Location;
import de.tu_berlin.dima.niteout.pt.data.Segment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Returns data from the google directions API and converts them to segments
 */
public class GoogleApiWrapper implements PublicTransportDataSource {

    private String apiKey;

    public GoogleApiWrapper(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public List<Segment> getDirections(Location start, Location destination, LocalDate departureTime) {

        // creating context
        GeoApiContext context = new GeoApiContext().setApiKey(apiKey);

        // getting directions from api
        DirectionsResult directions = null;
        try {
            directions = DirectionsApi.getDirections(context,
                        start.toString(),
                        destination.toString()).await();
            System.out.println(directions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
