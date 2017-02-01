package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
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

    /**
     * A map of routing strategies for the different modes of transport
     */
    private static final HashMap<TransportMode, RoutingStrategy> routingStrategies = new HashMap<>();
    static {
    	routingStrategies.put(TransportMode.PUBLIC_TRANSPORT, new HereApiWrapper(Settings.getHereApiAppID(), Settings.getHereApiAppCode()));
    	routingStrategies.put(TransportMode.WALKING, new MapzenApiWrapper(Settings.getMapzenApiKey()));
    }

    private RoutingStrategy getRoutingStrategy(TransportMode transportMode) {
        return routingStrategies.get(transportMode);
    }
    

    @Override
    public int getTripTime(TransportMode transportMode,
                           Location startLocation, Location destinationLocation,
                           LocalDateTime startTime) throws IOException {
    	return getRoutingStrategy(transportMode).getTripTime(startLocation, destinationLocation, startTime);
    }

    @Override
    public RouteSummary getRouteSummary(TransportMode transportMode,
                                        Location startLocation, Location destinationLocation,
                                        LocalDateTime startTime) throws IOException {
    	return getRoutingStrategy(transportMode).getRouteSummary(startLocation, destinationLocation, startTime);
    }

    @Override
    public List<TimeMatrixEntry> getMatrix(TransportMode transportMode,
                                           Location[] startLocations, Location[] destinationLocations,
                                           LocalDateTime startTime) throws IOException {
        return getRoutingStrategy(transportMode).getMatrix(startLocations, destinationLocations, startTime);
    }
}