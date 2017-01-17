package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.Route;

import java.time.LocalDateTime;

// TODO document methods
public interface RoutingAPI {

    int getPublicTransportTripTime(Location start, Location destination, LocalDateTime startTime);

    int getWalkingTripTime(Location start, Location destination);


    Route getPublicTransportDirections(Location start, Location destination, LocalDateTime startTime);

    Route getWalkingDirections(Location start, Location destination);
}
