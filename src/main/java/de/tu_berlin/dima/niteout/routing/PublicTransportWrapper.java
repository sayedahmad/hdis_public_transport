package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.Route;
import de.tu_berlin.dima.niteout.routing.model.RouteSummary;
import de.tu_berlin.dima.niteout.routing.model.TimeMatrixEntry;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicTransportWrapper {
    int getPublicTransportTripTime(Location start, Location destination, LocalDateTime departure) throws
            RoutingAPIException;

    Route getPublicTransportDirections(Location start, Location destination, LocalDateTime departure) throws
            RoutingAPIException;

    List<TimeMatrixEntry> getMultiModalMatrix(Location[] startLocations, Location[] destinationLocations,
                                              LocalDateTime departureTime) throws RoutingAPIException;

    RouteSummary getPublicTransportRouteSummary(Location start, Location destination, LocalDateTime departure)
            throws RoutingAPIException;
}
