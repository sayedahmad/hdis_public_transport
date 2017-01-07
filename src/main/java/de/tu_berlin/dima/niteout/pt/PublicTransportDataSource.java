package de.tu_berlin.dima.niteout.pt;

import de.tu_berlin.dima.niteout.pt.data.Location;
import de.tu_berlin.dima.niteout.pt.data.Segment;

import java.time.LocalDate;
import java.util.List;

/**
 * Returns directions from a data source like a API
 */
public interface PublicTransportDataSource {

    List<Segment> getDirections(Location start, Location destination, LocalDate departureTime);
}
