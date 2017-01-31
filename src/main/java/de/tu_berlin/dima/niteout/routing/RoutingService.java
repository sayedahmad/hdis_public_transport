package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service to process routing requests.
 * <p>
 * This class typically takes input of locations and times and returns options of different rides using the public
 * transportation in Berlin.
 * <p>
 * It uses external APIs to serve requests.
 */
public class RoutingService implements RoutingAPI {

    private static class Settings {

        static final String getHereApiAppID() {
            return System.getProperty("API_HERE_APP_ID");
        }

        static final String getHereApiAppCode() {
            return System.getProperty("API_HERE_APP_CODE");
        }

        static final String getMapzenApiKey() {
            return System.getProperty("API_KEY_MAPZEN");
        }
    }


    private PublicTranportAPI publicTranportAPI;
    private WalkingDirectionsAPI walkingDirectionsAPI;

    // API lazy initialization
    private PublicTranportAPI getPublicTransportAPI() {
        if (publicTranportAPI == null) {
            // injection - TODO discuss if we use "proper" injection
            publicTranportAPI = new HereApiWrapper(Settings.getHereApiAppID(), Settings.getHereApiAppCode());
        }
        return publicTranportAPI;
    }

    private WalkingDirectionsAPI getWalkingDirectionsAPI() {
        if (walkingDirectionsAPI == null) {
            walkingDirectionsAPI = new MapzenApiWrapper(Settings.getMapzenApiKey());
        }
        return walkingDirectionsAPI;
    }

    /**
     * The time in seconds to travel from one location to another via Public Transport
     *
     * @param start       The starting location
     * @param destination The destination location
     * @param startTime   The time at which the journey will begin
     * @return The total travel time in seconds
     */
    private int getPublicTransportTripTime(Location start, Location destination, LocalDateTime startTime) {
        return getPublicTransportAPI().getPublicTransportTripTime(start, destination, startTime);
    }

    /**
     * The time in seconds to walk from one location to another
     *
     * @param start       The starting location
     * @param destination The destination location
     * @return The travel time in seconds
     */
    private int getWalkingTripTime(Location start, Location destination) throws IOException {

        return getWalkingDirectionsAPI().getWalkingTripTime(start, destination);
    }

    private RouteSummary getPublicTransportRouteSummary(Location start, Location destination, LocalDateTime startTime) {
        return getPublicTransportAPI().getPublicTransportRouteSummary(start, destination, startTime);
    }

    private RouteSummary getWalkingRouteSummary(Location start, Location destination, LocalDateTime startTime) throws IOException {

        RouteSummary routeSummary = getWalkingDirectionsAPI().getWalkingRouteSummary(start, destination, startTime);
        return routeSummary;
    }

    @Override
    public int getTripTime(TransportMode transportMode,
                           Location startLocation, Location destinationLocation,
                           LocalDateTime startTime) throws IOException {
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
    public RouteSummary getRouteSummary(TransportMode transportMode,
                                        Location startLocation, Location destinationLocation,
                                        LocalDateTime startTime) throws IOException {
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
    public List<TimeMatrixEntry> getMatrix(TransportMode transportMode,
                                           Location[] startLocations, Location[] destinationLocations,
                                           LocalDateTime startTime) throws IOException {
        switch (transportMode) {

            case PUBLIC_TRANSPORT:
                return getPublicTransportAPI().getMultiModalMatrix(startLocations, destinationLocations, startTime);

            case WALKING:
                return getWalkingDirectionsAPI().getWalkingMatrix(startLocations, destinationLocations);

            default:
                throw new IllegalArgumentException("transportMode");
        }
    }
}