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

    /**
     * The time in seconds to travel from one location to another via Public Transport
     * @param start The starting location
     * @param destination The destination location
     * @param startTime The time at which the journey will begin
     * @return The total travel time in seconds
     */
    @Override
    public int getPublicTransportTime(Location start, Location destination, LocalDateTime startTime) {
        // TODO implement
        return 0;
    }

    /**
     * The time in seconds to walk from one location to another
     * @param start The starting location
     * @param destination The destination location
     * @return The travel time in seconds
     */
    @Override
    public int getWalkingTime(Location start, Location destination) {
        // TODO implement
        return 0;
    }

    /**
     * The turn-by-turn direction to travel from one location to another via Public Transport at a specific time
     * @param start The start location
     * @param destination The destination location
     * @param startTime The time at which the journey will begin
     * @return The Route containing the directions
     */
    @Override
    public Route getPublicTransportDirections(Location start, Location destination, LocalDateTime startTime) {
        // TODO implement
        return null;
    }

    /**
     * The turn-by-torn directions to travel from one location to another by foot
     * @param start The start location
     * @param destination The destination location
     * @return The Route containing the directions
     */
    @Override
    public Route getWalkingDirections(Location start, Location destination) {
        // TODO implement
        return null;
    }
}
