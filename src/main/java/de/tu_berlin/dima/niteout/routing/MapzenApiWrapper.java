package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.TimeMatrixEntry;

import java.io.IOException;
import java.util.List;

/**
 * Created by aardila on 1/26/2017.
 */
public class MapzenApiWrapper {

    private final String apiKey;

    public MapzenApiWrapper(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getWalkingTripTime(Location start, Location destination) throws IOException {
        MapzenMobilityApiWrapper mobilityWrapper = new MapzenMobilityApiWrapper(apiKey);
        return mobilityWrapper.getWalkingTripTime(start, destination);
    }

    public List<TimeMatrixEntry> getWalkingMatrix(Location[] startLocations, Location[] destinationLocations) throws IOException {

        MapzenMatrixApiWrapper matrixWrapper = new MapzenMatrixApiWrapper(this.apiKey);
        return matrixWrapper.getWalkingMatrix(startLocations, destinationLocations);
    }
}
