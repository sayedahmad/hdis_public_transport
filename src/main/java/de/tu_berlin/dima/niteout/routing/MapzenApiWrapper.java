package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.RouteSummary;
import de.tu_berlin.dima.niteout.routing.model.TimeMatrixEntry;
import de.tu_berlin.dima.niteout.routing.model.TransportMode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * An implementation of the {@link RoutingStrategy} as a facade to other Mapzen APIs (like Mobility and Matrix)
 * @author Andres Ardila
 */
class MapzenApiWrapper implements RoutingStrategy {

    private final String apiKey;

    public MapzenApiWrapper(String apiKey) {
        this.apiKey = apiKey;
    }
    
    @Override
    public TransportMode getTransportMode() {
    	return TransportMode.WALKING;
    }

    @Override
    public int getTripTime(Location start, Location destination) throws IOException {
        return getTripTime(start, destination, null);
    }

    @Override
    public int getTripTime(Location start, Location destination,
                           LocalDateTime departureTime) throws IOException {
        MapzenMobilityApiWrapper mobilityWrapper = new MapzenMobilityApiWrapper(apiKey);
        return departureTime == null ?
                mobilityWrapper.getWalkingTripTime(start, destination) :
                mobilityWrapper.getWalkingTripTime(start, destination, departureTime);
    }

    @Override
    public RouteSummary getRouteSummary(Location start, Location destination) throws IOException {
        return getRouteSummary(start, destination, null);
    }

    @Override
    public RouteSummary getRouteSummary(Location start, Location destination,
                                        LocalDateTime departureTime) throws IOException {
        MapzenMobilityApiWrapper mobilityWrapper = new MapzenMobilityApiWrapper(apiKey);
        return departureTime == null ?
                mobilityWrapper.getWalkingRouteSummary(start, destination) :
                mobilityWrapper.getWalkingRouteSummary(start, destination, departureTime);
    }

    @Override
    public List<TimeMatrixEntry> getMatrix(Location[] startLocations,
                                           Location[] destinationLocations) throws IOException {

        return this.getMatrix(startLocations, destinationLocations, null);
    }
    
    @Override
    public List<TimeMatrixEntry> getMatrix(Location[] startLocations,
                                           Location[] destinationLocations,
                                           LocalDateTime departureTime) throws IOException {

        MapzenMatrixApiWrapper matrixWrapper = new MapzenMatrixApiWrapper(this.apiKey);
        return matrixWrapper.getWalkingMatrix(startLocations, destinationLocations, departureTime);
    }
}
