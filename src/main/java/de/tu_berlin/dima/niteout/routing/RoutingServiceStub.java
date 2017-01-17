package de.tu_berlin.dima.niteout.routing;


import de.tu_berlin.dima.niteout.routing.data.Location;
import de.tu_berlin.dima.niteout.routing.data.Route;
import de.tu_berlin.dima.niteout.routing.data.Segment;
import de.tu_berlin.dima.niteout.routing.data.TransportMode;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class RoutingServiceStub implements RoutingAPI {

    public final static Location ALEX = new Location(52.521918, 13.412818);
    public final static Location BRANDENBURGER_TOR = new Location(52.516298, 13.378482);
    public final static Location ERNST_REUTER = new Location(52.512489, 13.323322);
    public final static Location TU_EN_BUILDING = new Location(52.515563, 13.326857);

    public final static LocalDateTime PUBLIC_TRANSPORT_TRIP_START_TIME = LocalDateTime.of(2017, Month.JANUARY, 19, 10, 10);

    private final static int PT_ROUTE_DURATION = 15;

    @Override
    public int getPublicTransportTripTime(Location start, Location destination, LocalDateTime startTime) {
        if (equalsPTTripProposedRoute(start, destination, startTime)) {
            return PT_ROUTE_DURATION;
        }
        throw new IllegalArgumentException("Please only use the following locations as route: start "
                + "'52.521918, 13.412818' (alex) and destination '52.516298, 13.378482' (brandenburger tor) with "
                + "departure time 10:10 on 19th of january 2017");
    }

    @Override
    public int getWalkingTripTime(Location start, Location destination) {
        if (equalsWalkProposedRoute(start, destination)) {

        }
        throw new IllegalArgumentException("Please only use the following locations as route: start "
                + "'52.515563, 13.326857' (ernst-reuter) and destination '52.515563, 13.326857' (EN building)");
    }

    @Override
    public Route getPublicTransportDirections(Location start, Location destination, LocalDateTime startTime) {
        if (equalsPTTripProposedRoute(start, destination, startTime)) {
            Segment segment = new Segment();
            segment.setDepartureTime(LocalDateTime.of(2017, Month.JANUARY, 19, 10, 12, 38));
            segment.setArrivalTime(LocalDateTime.of(2017, Month.JANUARY, 19, 10, 26, 43));
            segment.setStartLocation(new Location(52.5222551, 13.4132278));
            segment.setDestinationLocation(new Location(52.5162178, 13.378496));
            segment.setMode(TransportMode.PUBLIC_TRANSPORT);

            List<Segment> segments = new ArrayList<Segment>();
            segments.add(segment);

            Route route = new Route();
            route.setDuration(PT_ROUTE_DURATION);
            route.setSegments(segments);

            return route;

        }
        throw new IllegalArgumentException("Please only use the following locations as route: start "
                + "'52.521918, 13.412818' (alex) and destination '52.516298, 13.378482' (brandenburger tor) with "
                + "departure time 10:10 on 19th of january 2017");
    }

    @Override
    public Route getWalkingDirections(Location start, Location destination) {
        if (equalsWalkProposedRoute(start, destination)) {

        }
        throw new IllegalArgumentException("Please only use the following locations as route: start "
                + "'52.515563, 13.326857' (ernst-reuter) and destination '52.515563, 13.326857' (EN building)");
    }

    private static boolean equalsWalkProposedRoute(Location start, Location destination) {
        return start.equals(ERNST_REUTER) && destination.equals(TU_EN_BUILDING);
    }

    private static boolean equalsPTTripProposedRoute(Location start, Location destination, LocalDateTime startTime) {
        return start.equals(ALEX) && destination.equals(BRANDENBURGER_TOR)
                && startTime.equals(PUBLIC_TRANSPORT_TRIP_START_TIME);
    }
}
