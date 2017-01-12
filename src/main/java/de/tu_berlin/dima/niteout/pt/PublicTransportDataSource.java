package de.tu_berlin.dima.niteout.pt;

import de.tu_berlin.dima.niteout.pt.data.Location;
import de.tu_berlin.dima.niteout.pt.data.PublicTransportException;
import de.tu_berlin.dima.niteout.pt.data.Segment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Returns directions from a data source like a API
 */
public interface PublicTransportDataSource {

    /**
     * Requests the data source for information about directions between two locations at a specific time.
     *
     * @param start where the trips starts at
     * @param destination where the trip should end at
     * @param departureTime when the trip should start at
     * @return a list of segments, containing information about the duration and type of transportation
     */
    List<Segment> getDirections(Location start, Location destination, LocalDateTime departureTime) throws PublicTransportException;
}
