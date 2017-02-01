/**
 * 
 */
package de.tu_berlin.dima.niteout.routing;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.RouteSummary;
import de.tu_berlin.dima.niteout.routing.model.TimeMatrixEntry;
import de.tu_berlin.dima.niteout.routing.model.TransportMode;

/**
 * @author Andres Ardila
 *
 */
public interface RoutingStrategy {
	
	/**
	 * The TransportMode used by this strategy
	 * @return
	 */
	TransportMode getTransportMode();

    int getTripTime(Location startLocation, Location destinationLocation) throws IOException;
    int getTripTime(Location startLocation, Location destinationLocation, LocalDateTime startTime) throws IOException;

	RouteSummary getRouteSummary(Location start, Location destination) throws IOException;
	RouteSummary getRouteSummary(Location start, Location destination, LocalDateTime departureTime) throws IOException;

	List<TimeMatrixEntry> getMatrix(Location[] startLocations, Location[] destinationLocations) throws IOException;
	List<TimeMatrixEntry> getMatrix(Location[] startLocations, Location[] destinationLocations, LocalDateTime departureTime) throws IOException;
}
