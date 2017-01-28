package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

// TODO document methods
public interface RoutingAPI {

    //int getPublicTransportTripTime(Location start, Location destination, LocalDateTime startTime);

    //int getWalkingTripTime(Location start, Location destination) throws IOException;

    int getTripTime(TransportMode transportMode,
                    Location startLocation, Location destinationLocation,
                    LocalDateTime startTime) throws IOException;


    //Route getPublicTransportDirections(Location start, Location destination, LocalDateTime startTime);
    //Route getWalkingDirections(Location start, Location destination);

    //RouteSummary getPublicTransportRouteSummary(Location start, Location destination, LocalDateTime startTime);

    //RouteSummary getWalkingRouteSummary(Location start, Location destination);

    RouteSummary getRouteSummary(TransportMode transportMode,
                                 Location startLocation, Location destinationLocation,
                                 LocalDateTime startTime) throws IOException;

    List<TimeMatrixEntry> getMatrix(TransportMode transportMode,
                                    Location[] startLocations, Location[] destinationLocations,
                                    LocalDateTime startTime) throws IOException;
}
