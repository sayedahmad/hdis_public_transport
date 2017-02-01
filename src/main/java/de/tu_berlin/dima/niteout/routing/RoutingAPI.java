package de.tu_berlin.dima.niteout.routing;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.RouteSummary;
import de.tu_berlin.dima.niteout.routing.model.TimeMatrixEntry;
import de.tu_berlin.dima.niteout.routing.model.TransportMode;


public interface RoutingAPI {

	/**
	 * Get the amount of time (in seconds) to travel between two locations with the specified transport mode (e.g. walking)
	 * @param transportMode the mode of transport for the trip (e.g. walking, public transport)
	 * @param startLocation the location where the trip starts
	 * @param destinationLocation the location where the trip terminates
	 * @param startTime the date and time at which the trip starts
	 * @return the total number of seconds required for the trip 
	 * @throws IOException
	 */
    int getTripTime(TransportMode transportMode,
                    Location startLocation, Location destinationLocation,
                    LocalDateTime startTime) throws IOException;

    /**
     * Gets the summary details about a route between two locations with the specified transport mode (e.g. walking) 
     * @param transportMode the mode of transport for the trip (e.g. walking, public transport)
     * @param startLocation the location where the trip starts
     * @param destinationLocation the location where the trip terminates
     * @param startTime the date and time at which the trip starts
     * @return a RouteSummary with the details about the route
     * @throws IOException
     */
    RouteSummary getRouteSummary(TransportMode transportMode,
                                 Location startLocation, Location destinationLocation,
                                 LocalDateTime startTime) throws IOException;

    /**
     * Gets a matrix with trip information between start and destination locations
     * @param transportMode the mode of transport for the trip (e.g. walking, public transport)
     * @param startLocations the locations where the trips start
     * @param destinationLocations the locations where the trips terminate
     * @param startTime the date and time at which the trips start
     * @return a matrix with trip information between start and destination locations
     * @throws IOException
     */
    List<TimeMatrixEntry> getMatrix(TransportMode transportMode,
                                    Location[] startLocations, Location[] destinationLocations,
                                    LocalDateTime startTime) throws IOException;
}
