package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

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
    private int getPublicTransportTripTime(Location start, Location destination, LocalDateTime startTime) {
        return getPublicTransportAPI().getPublicTransportTripTime(start, destination, startTime);
    }

    /**
     * The time in seconds to walk from one location to another
     * @param start The starting location
     * @param destination The destination location
     * @return The travel time in seconds
     */
    private int getWalkingTripTime(Location start, Location destination) throws IOException {

        MapzenApiWrapper apiWrapper = new MapzenApiWrapper(System.getProperty("API_KEY_MAPZEN"));
        return apiWrapper.getWalkingTripTime(start, destination);
    }

    /**
     * The turn-by-turn direction to travel from one location to another via Public Transport at a specific time
     * @param start The start location
     * @param destination The destination location
     * @param startTime The time at which the journey will begin
     * @return The Route containing the directions
     */
    private Route getPublicTransportDirections(Location start, Location destination, LocalDateTime startTime) {
        // TODO do we have to implement this?
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * The turn-by-turn directions to travel from one location to another by foot
     * @param start The start location
     * @param destination The destination location
     * @return The Route containing the directions
     */
    private Route getWalkingDirections(Location start, Location destination) {
        // TODO implement
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private RouteSummary getPublicTransportRouteSummary(Location start, Location destination, LocalDateTime startTime) {
        return getPublicTransportAPI().getPublicTransportRouteSummary(start, destination, startTime);
    }

    private RouteSummary getWalkingRouteSummary(Location start, Location destination, LocalDateTime startTime) throws IOException {

        MapzenApiWrapper apiWrapper = new MapzenApiWrapper(System.getProperty("API_KEY_MAPZEN"));
        RouteSummary routeSummary = apiWrapper.getWalkingRouteSummary(start, destination, startTime);

        return routeSummary;
    }

    @Override
    public int getTripTime(TransportMode transportMode, Location startLocation, Location destinationLocation, LocalDateTime startTime) throws IOException {
        switch (transportMode) {

            case PUBLIC_TRANSPORT:
                return this.getPublicTransportTripTime(startLocation, destinationLocation, startTime);

            case WALKING:
                return this.getWalkingTripTime(startLocation, destinationLocation);

            default:
                throw new IllegalArgumentException("transportMode");
        }
    }

    @Override
    public RouteSummary getRouteSummary(TransportMode transportMode, Location startLocation, Location destinationLocation, LocalDateTime startTime) throws IOException {
        switch (transportMode) {

            case PUBLIC_TRANSPORT:
                return this.getPublicTransportRouteSummary(startLocation, destinationLocation, startTime);

            case WALKING:
                return this.getWalkingRouteSummary(startLocation, destinationLocation, startTime);

            default:
                throw new IllegalArgumentException("transportMode");
        }
    }

    @Override
    public List<TimeMatrixEntry> getMatrix(TransportMode transportMode, Location[] startLocations, Location[] destinationLocations, LocalDateTime startTime) throws IOException {

        switch (transportMode) {

            case PUBLIC_TRANSPORT:
                // TODO implement
                throw new UnsupportedOperationException("Not yet implemented");

            case WALKING:
                MapzenApiWrapper wrapper = new MapzenApiWrapper(System.getProperty("API_KEY_MAPZEN"));
                return wrapper.getWalkingMatrix(startLocations, destinationLocations);

            default:
                throw new IllegalArgumentException("transportMode");
        }
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