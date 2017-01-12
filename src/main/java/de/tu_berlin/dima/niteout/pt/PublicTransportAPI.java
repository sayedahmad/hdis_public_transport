package de.tu_berlin.dima.niteout.pt;

import de.tu_berlin.dima.niteout.pt.data.Location;
import de.tu_berlin.dima.niteout.pt.data.Route;

import java.time.LocalDateTime;

// TODO document methods
public interface PublicTransportAPI {

    int getPublicTransportTime(Location start, Location destination, LocalDateTime startTime);

    int getWalkingTime(Location start, Location destination);



    Route getPublicTransportDirections(Location start, Location destination, LocalDateTime startTime);

    Route getWalkingDirections(Location start, Location destination);

}


