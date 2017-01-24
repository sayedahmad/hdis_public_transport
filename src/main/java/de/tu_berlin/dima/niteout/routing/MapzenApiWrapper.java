package de.tu_berlin.dima.niteout.routing;


import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.RouteSummary;
import de.tu_berlin.dima.niteout.routing.model.TransportMode;
import de.tu_berlin.dima.niteout.routing.model.mapzen.CostingModel;

import javax.json.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


class MapzenApiWrapper {

    private final String apiKey;
    private final String uriFormat = "https://valhalla.mapzen.com/route?json=%s&api_key=%s";

    private static final DateTimeFormatter ISO8601_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("YYYY-MM-DD'T'HH:mm");


    public MapzenApiWrapper(String apiKey) {

        if (apiKey == null || apiKey.trim().length() == 0)
            throw new IllegalArgumentException("apiKey cannot be null or empty");

        this.apiKey = apiKey;
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
        //instead of the correct HH:MM:SS-01:00, it returns HH:MM:SS01:00
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

    private String fixDateTimeString(String dateTimeString) {
        return dateTimeString.substring(0,16)+"+"+dateTimeString.substring(16);
    }

    public int getWalkingTripTime(Location start, Location destination) throws Exception {
        int tripDuration = -1;

        try {
            JsonObject response = getRouteResponse(start, destination, CostingModel.PEDESTRIAN);
            tripDuration = response.getJsonObject("trip").getJsonObject("summary").getInt("time");
        } catch (MalformedURLException e) {
            e.printStackTrace(); //TODO
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        }

        return tripDuration;
    }

    public int getPublicTransportTripTime(Location start, Location destination, LocalDateTime departureTime) {
        int tripDuration = -1;

        try {
            //WARNING: multimodal currently supports pedestrian and transit. In the future, multimodal will return a combination of all modes of transport (including auto).
            JsonObject response = getRouteResponse(start, destination, CostingModel.MULTIMODAL, departureTime);
            tripDuration = response.getJsonObject("trip").getJsonObject("summary").getInt("time");
        } catch (MalformedURLException e) {
            e.printStackTrace(); //TODO
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        }

        return tripDuration;
    }

    private JsonObject getRouteResponse(Location start, Location destination, CostingModel costingModel) throws MalformedURLException, IOException {
        return getRouteResponse(start, destination, costingModel, null);
    }

    private JsonObject getRouteResponse(
            Location start, Location destination,
            CostingModel costingModel, LocalDateTime departureTime) throws MalformedURLException, IOException {

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

        String uri = String.format(this.uriFormat, json, this.apiKey);
        URL url = new URL(uri);
        InputStream is = url.openStream();
        JsonReader jsonReader = Json.createReader(is);
        JsonObject jsonObject = jsonReader.readObject();

        return jsonObject;
    }
}
