package de.tu_berlin.dima.niteout.pt;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import de.tu_berlin.dima.niteout.pt.data.Location;
import de.tu_berlin.dima.niteout.pt.data.Segment;
import de.tu_berlin.dima.niteout.pt.data.TransportMode;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Returns data from the google directions API and converts them to segments
 */
public class GoogleApiWrapper implements PublicTransportDataSource {

    private String apiKey;

    /**
     * Creates a new wrapper of a data source for public transporting using the Google API
     * @param apiKey the API key for the google API
     */
    public GoogleApiWrapper(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public List<Segment> getDirections(Location start, Location destination, LocalDateTime departureTime) {

        // creating context
        GeoApiContext context = new GeoApiContext().setApiKey(apiKey);

        // getting directions from api
        DirectionsResult directions = null;
        try {
            directions = DirectionsApi.getDirections(context, start.toString(), destination.toString())
                    .mode(TravelMode.TRANSIT).departureTime(toJodaTime(departureTime)).await();

            System.out.println(directions);
        } catch (Exception e) {
            // TODO E raise error
        }

        if (directions != null) {
            List<Segment> segments = new ArrayList<>();

            // Go only through first route, google already sorts for best one
            // and anyways we do not make the call with alternatives, we only
            // ask for one route
            // TODO D add url sources from google api
            for (DirectionsLeg leg : directions.routes[0].legs) {
                // TODO E raise error when array empty
                segments.add(legToSegment(leg));
            }

            // TODO E raise error when no segments found
            return segments;
        }
        // TODO E raise error instead of returning an empty list
        return new ArrayList<>();
    }

    private Segment legToSegment(DirectionsLeg leg) {
        Segment segment = new Segment();

        // TODO DISCUSS in next meeting: google optimized start location or input one?
        segment.setStartLocation(latLngToLocation(leg.startLocation));
        segment.setDestinationLocation(latLngToLocation(leg.endLocation));
        if (leg.departureTime != null) {
            segment.setDepartureTime(toJavaTime(leg.departureTime));
        } else {
            // TODO change later
            System.out.println("Warning: no departure time was set");
        }
        if (leg.arrivalTime != null) {
            segment.setArrivalTime(toJavaTime(leg.arrivalTime));
        } else {
            // TODO change later
            System.out.println("Warning: no arrival time was set");
        }

        boolean foundPublicTransport = Arrays.stream(leg.steps).anyMatch(
                step -> step.travelMode.equals(TravelMode.TRANSIT));

        segment.setMode(foundPublicTransport ? TransportMode.PUBLIC_TRANSPORT : TransportMode.WALKING);

        return segment;
    }

    // STATIC CONVERSION HELPER

    /**
     * Transforms a google LatLng to a Location
     */
    public static Location latLngToLocation(LatLng latLng) {
        return new Location(latLng.lat, latLng.lng);
    }

    /**
     * Converts an org.joda.time.DateTime to a java.time.LocalDateTime
     * @param jodaTime joda time to be converted
     * @return the converted java time
     */
    public static LocalDateTime toJavaTime(org.joda.time.DateTime jodaTime) {
        // TODO: write test for this method! - @Sahim
        return LocalDateTime.of(
                jodaTime.getYear(),
                jodaTime.getMonthOfYear(),
                jodaTime.getDayOfMonth(),
                jodaTime.getHourOfDay(),
                jodaTime.getMinuteOfHour(),
                jodaTime.getSecondOfMinute(),
                jodaTime.getMillisOfSecond() * 1_000_000);
    }

    /**
     * Converts an java.time.DateTime to a org.joda.time.DateTime
     * @param javaTime the java time to be converted
     * @return the converted joda DateTime
     */
    public static org.joda.time.DateTime toJodaTime(LocalDateTime javaTime) {
        // TODO: write test for this method!
        return new org.joda.time.DateTime(javaTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }
}
