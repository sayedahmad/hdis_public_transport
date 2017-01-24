package de.tu_berlin.dima.niteout.routing;


import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.mapzen.CostingModel;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;


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

    private JsonObject getRouteResponse(Location start, Location destination, CostingModel costingModel) throws MalformedURLException, IOException {
        return getRouteResponse(start, destination, costingModel, null);
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