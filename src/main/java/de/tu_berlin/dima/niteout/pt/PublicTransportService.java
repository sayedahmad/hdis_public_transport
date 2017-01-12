package de.tu_berlin.dima.niteout.pt;

import de.tu_berlin.dima.niteout.pt.data.Location;
import de.tu_berlin.dima.niteout.pt.data.Route;

import java.time.LocalDateTime;

/**
 * Service, that processes Public Transport requests.
 *
 * This class typically takes input of locations and times and returns options of different rides using the public
 * transportation in Berlin.
 *
 * It uses the Google Maps Directions API to acquire necessary information.
 */
public class PublicTransportService implements PublicTransportAPI {

    @Override
    public int getPublicTransportTime(Location start, Location destination, LocalDateTime startTime) {
        // TODO implement
        return 0;
    }

    @Override
    public int getWalkingTime(Location start, Location destination) {
        // TODO implement
        return 0;
    }

    @Override
    public Route getPublicTransportDirections(Location start, Location destination, LocalDateTime startTime) {
        // TODO implement
        return null;
    }

    @Override
    public Route getWalkingDirections(Location start, Location destination) {
        // TODO implement
        return null;
    }
}
