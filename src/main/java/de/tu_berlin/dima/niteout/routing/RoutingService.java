package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.Route;
import de.tu_berlin.dima.niteout.routing.model.RouteSummary;

import java.time.LocalDateTime;

/**
 * Service to process routing requests.
 *
 * This class typically takes input of locations and times and returns options of different rides using the public
 * transportation in Berlin.
 *
 * It uses external APIs to serve requests.
 */
public class RoutingService implements RoutingAPI {
    // TODO move javadocs to Interface

    private static final String API_HERE_APP_ID = "API_HERE_APP_ID";
    private static final String API_HERE_APP_CODE = "API_HERE_APP_CODE";
    private PublicTranportAPI publicTranportAPI;


    /**
     * The time in seconds to travel from one location to another via Public Transport
     * @param start The starting location
     * @param destination The destination location
     * @param startTime The time at which the journey will begin
     * @return The total travel time in seconds
     */
    @Override
    public int getPublicTransportTripTime(Location start, Location destination, LocalDateTime startTime) {
        return getPublicTransportAPI().getPublicTransportTripTime(start, destination, startTime);
    }

    /**
     * The time in seconds to walk from one location to another
     * @param start The starting location
     * @param destination The destination location
     * @return The travel time in seconds
     */
    @Override
    public int getWalkingTripTime(Location start, Location destination) {
        // TODO implement
        throw new UnsupportedOperationException("Not yet implemented");
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
        // TODO do we have to implement this?
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * The turn-by-turn directions to travel from one location to another by foot
     * @param start The start location
     * @param destination The destination location
     * @return The Route containing the directions
     */
    @Override
    public Route getWalkingDirections(Location start, Location destination) {
        // TODO implement
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public RouteSummary getPublicTransportRouteSummary(Location start, Location destination, LocalDateTime startTime) {
        return getPublicTransportAPI().getPublicTransportRouteSummary(start, destination, startTime);
    }

    @Override
    public RouteSummary getWalkingRouteSummary(Location start, Location destination) {
        // TODO implement
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // for lazy initialize
    private PublicTranportAPI getPublicTransportAPI() {
        if (publicTranportAPI == null) {
            // injection - TODO discuss if we use "proper" injection
            publicTranportAPI = new HereApiWrapper(System.getProperty(API_HERE_APP_ID), System.getProperty(API_HERE_APP_CODE));
        }
        return publicTranportAPI;
    }
}
