package de.tu_berlin.dima.niteout.routing;


import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.RouteSummary;
import de.tu_berlin.dima.niteout.routing.model.TransportMode;
import de.tu_berlin.dima.niteout.routing.model.mapzen.CostingModel;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Optional;
import javax.json.*;


class MapzenMobilityApiWrapper extends MapzenApi {

//    private final String apiKey;
//    private final String uriFormat = "https://valhalla.mapzen.com/route?json=%s&api_key=%s";

    private static final DateTimeFormatter ISO8601_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("YYYY-MM-DD'T'HH:mm");


    public MapzenMobilityApiWrapper(String apiKey) {

        super("valhalla", apiKey);

        if (apiKey == null || apiKey.trim().length() == 0)
            throw new IllegalArgumentException("apiKey cannot be null or empty");

//        this.apiKey = apiKey;
    }

    public int getWalkingTripTime(Location start, Location destination) throws IOException {

        return getWalkingTripTime(start, destination, null);
    }

    public int getWalkingTripTime(Location start, Location destination, LocalDateTime departureTime) throws IOException {

        JsonObject response = departureTime == null ?
                getRouteResponse(start, destination, CostingModel.PEDESTRIAN) :
                getRouteResponse(start, destination, CostingModel.PEDESTRIAN, departureTime);
        int tripDuration = response.getJsonObject("trip").getJsonObject("summary").getInt("time");

        return tripDuration;
    }

    public int getPublicTransportTripTime(Location start, Location destination, LocalDateTime departureTime) throws IOException {

        //WARNING: multimodal currently supports pedestrian and transit. In the future, multimodal will return a combination of all modes of transport (including auto).
        JsonObject response = getRouteResponse(start, destination, CostingModel.MULTIMODAL, departureTime);
        int tripDuration = response.getJsonObject("trip").getJsonObject("summary").getInt("time");

        return tripDuration;
    }

    public RouteSummary getWalkingRouteSummary(Location start, Location destination) throws IOException {

        JsonObject response = getRouteResponse(start, destination, CostingModel.PEDESTRIAN);
        RouteSummary routeSummary = new RouteSummary();
        int tripDuration = response.getJsonObject("trip").getJsonObject("summary").getInt("time");
        routeSummary.setTotalDuration(tripDuration);
        HashMap<TransportMode, Integer> hashMap = new HashMap<>();
        hashMap.put(TransportMode.WALKING, tripDuration);
        routeSummary.setModeOfTransportTravelTimes(hashMap);

        return routeSummary;
    }

    public RouteSummary getWalkingRouteSummary(Location start, Location destination, LocalDateTime dateTime) throws IOException {
        JsonObject response = getRouteResponse(start, destination, CostingModel.PEDESTRIAN, dateTime);
        JsonObject summaryJsonObject = response.getJsonObject("trip").getJsonObject("summary");

        RouteSummary routeSummary = new RouteSummary();

        int tripDuration = summaryJsonObject.getInt("time");
        double distance = summaryJsonObject.getJsonNumber("length").doubleValue();
        JsonArray locations = response.getJsonObject("trip").getJsonArray("locations");
        JsonObject startLocation = (JsonObject)locations.get(0);
        JsonObject arrivalLocation = (JsonObject)locations.get(1);

        //HACK there is a bug whereby timezones w/ negative UTC offsets are not formatted correctly
        //instead of the correct HH:MM:SS+01:00, it returns HH:MM:SS01:00
        //see https://github.com/valhalla/valhalla/issues/13
        String departureDateTimeString = startLocation.getString("date_time");
        //check if the bug is still there in case they fix it
        if (departureDateTimeString.charAt(16) != '+') {
            departureDateTimeString = fixDateTimeString(departureDateTimeString);
        }
        String arrivalDateTimeString = arrivalLocation.getString("date_time");
        if (arrivalDateTimeString.charAt(16) != '+') {
            arrivalDateTimeString = fixDateTimeString(arrivalDateTimeString);
        }
        OffsetDateTime departureDateTime = OffsetDateTime.parse(departureDateTimeString);
        OffsetDateTime arrivalDateTime = OffsetDateTime.parse(arrivalDateTimeString);

        RouteSummary out = new RouteSummary();
        out.setTotalDuration(tripDuration);
        out.setDepartureTime(departureDateTime.toLocalDateTime());
        out.setArrivalTime(arrivalDateTime.toLocalDateTime());
        out.setTotalDistance(distance);
        HashMap<TransportMode, Integer> hashMap = new HashMap<>(1);
        hashMap.put(TransportMode.WALKING, tripDuration);

        return out;
    }

    /**
     * Fixes a bug with the Mapzen API returning invalid ISO 8601 date/time strings
     * @param dateTimeString
     * @return the corrected ISO-8601-compliant date/time string
     */
    private String fixDateTimeString(String dateTimeString) {
        return dateTimeString.substring(0,16)+"+"+dateTimeString.substring(16);
    }

    private JsonObject getRouteResponse(Location start, Location destination, CostingModel costingModel) throws IOException {
        return getRouteResponse(start, destination, costingModel, null);
    }

    private JsonObject getRouteResponse(
            Location start, Location destination,
            CostingModel costingModel, LocalDateTime departureTime) throws IOException {

        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
                .add("locations", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("lat", start.getLatitude())
                                .add("lon", start.getLongitude()))
                        .add(Json.createObjectBuilder()
                                .add("lat", destination.getLatitude())
                                .add("lon", destination.getLongitude())))
                .add("costing", costingModel.getApiString());

        if (departureTime != null) {

            jsonObjectBuilder.add("date_time", Json.createObjectBuilder()
                    .add("type", 1)
                    .add("value", departureTime.format(ISO8601_DATE_TIME_FORMATTER)));
        }

        JsonObject json = jsonObjectBuilder.build();

        JsonObject responseJsonObject = null;

        try {
            responseJsonObject = super.getResponse("route", json);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            //TODO
        }

        return responseJsonObject;
    }
}