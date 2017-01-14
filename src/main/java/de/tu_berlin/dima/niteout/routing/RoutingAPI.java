package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.data.Location;
import de.tu_berlin.dima.niteout.routing.data.Route;
import de.tu_berlin.dima.niteout.routing.data.TransportMode;

import java.time.LocalDateTime;

// TODO document methods
public interface RoutingAPI {

    int getPublicTransportTripTime(Location start, Location destination, LocalDateTime startTime);

    int getWalkingTripTime(Location start, Location destination);


    Route getPublicTransportDirections(Location start, Location destination, LocalDateTime startTime);

    Route getWalkingDirections(Location start, Location destination);
}
