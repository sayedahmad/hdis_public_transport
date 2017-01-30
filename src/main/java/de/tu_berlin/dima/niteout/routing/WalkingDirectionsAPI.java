package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.RouteSummary;
import de.tu_berlin.dima.niteout.routing.model.TimeMatrixEntry;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by aardila on 1/29/2017.
 */
public interface WalkingDirectionsAPI {

    int getWalkingTripTime(Location startLocation,
                           Location destinationLocation) throws IOException;

    int getWalkingTripTime(Location startLocation,
                           Location destinationLocation,
                           LocalDateTime startTime) throws IOException;

    RouteSummary getWalkingRouteSummary(Location start, Location destination) throws IOException;

    RouteSummary getWalkingRouteSummary(Location start, Location destination,
                                        LocalDateTime departureTime) throws IOException;

    List<TimeMatrixEntry> getWalkingMatrix(Location[] startLocations,
                                           Location[] destinationLocations) throws IOException;
}
