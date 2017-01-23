package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.TimeMatrixEntry;
import de.tu_berlin.dima.niteout.routing.model.mapzen.CostingModel;
import de.tu_berlin.dima.niteout.routing.model.mapzen.MatrixType;
import de.tu_berlin.dima.niteout.routing.model.mapzen.Units;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.json.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by aardila on 1/22/2017.
 */
class MapzenMatrixApiWrapper {

    private final String apiKey;
    private final String urlFormat = "https://matrix.mapzen.com/%s?json=%s&api_key=%s";
    private OkHttpClient httpClient;
    private final Units DistanceUnits = Units.KM;

    public MapzenMatrixApiWrapper(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient();
    }

    private JsonObject getResponse(MatrixType matrixType, JsonObject jsonObject) throws IOException {
        String url = String.format(this.urlFormat, matrixType.getApiString(), jsonObject, this.apiKey);
        Request request = new Request.Builder().url(url).build();

        Response response = this.httpClient.newCall(request).execute();

        JsonReader jsonReader = Json.createReader(response.body().charStream());

        return jsonReader.readObject();
    }

    /**
     * Gets a one-to-many time matrix between the starting location and all destinations
     * @param start the starting location
     * @param destinations the list of destinations
     * @return the time matrix
     * @throws IOException
     */
    public List<TimeMatrixEntry> getWalkingMatrix(Location start, Location[] destinations) throws IOException {

        JsonBuilder jsonBuilder = new JsonBuilder();
        jsonBuilder.addLocation(start);
        jsonBuilder.addLocations(destinations);
        JsonObject requestJsonObject = jsonBuilder.build(this.DistanceUnits);

        JsonObject response = this.getResponse(MatrixType.ONE_TO_MANY, requestJsonObject);
        //TODO check for error(s) in response
        String units = response.getString("units");
        JsonArray outerArray = response.getJsonArray(MatrixType.ONE_TO_MANY.getApiString());
        JsonArray innerArray = outerArray.getJsonArray(0);

        ArrayList<TimeMatrixEntry> out = new ArrayList<>(destinations.length);

        for (JsonValue value : innerArray) {
            JsonObject jsonObject = (JsonObject)value;
            int index = jsonObject.getInt("to_index");
            if (index == 0) { continue; } // skip 'from_index' : 0 'to_index' : 0 since it's the departure/start point

            TimeMatrixEntry entry = new TimeMatrixEntry(
                    0,
                    index - 1,
                    jsonObject.getInt("time"),
                    jsonObject.getJsonNumber("distance").doubleValue(),
                    units
            );
            out.add(entry);
        }

        return out;
    }

    public List<TimeMatrixEntry> getWalkingMatrix(
            Location[] startLocations, Location destinationLocation)
            throws IOException {

        JsonBuilder jsonBuilder = new JsonBuilder();
        jsonBuilder.addLocations(startLocations);
        jsonBuilder.addLocation(destinationLocation);
        JsonObject requestJsonObject = jsonBuilder.build(this.DistanceUnits);

        JsonObject response = this.getResponse(MatrixType.MANY_TO_ONE, requestJsonObject);
        //TODO check for error(s) in response
        String units = response.getString("units");

        ArrayList<TimeMatrixEntry> out = new ArrayList<>(startLocations.length);

        for (JsonValue jsonValue : response.getJsonArray(MatrixType.MANY_TO_ONE.getApiString())) {
            JsonArray innerArray = (JsonArray)jsonValue;
            JsonObject jsonObject = innerArray.getJsonObject(0);
            int fromIndex = jsonObject.getInt("from_index");
            if (fromIndex >= startLocations.length) continue; //skip last combination ("from destination to destination")

            TimeMatrixEntry entry = new TimeMatrixEntry(
                    fromIndex,
                    0,
                    jsonObject.getInt("time"),
                    jsonObject.getJsonNumber("distance").doubleValue(),
                    units
            );
            out.add(entry);
        }
        return out;

    }

    public List<TimeMatrixEntry> getWalkingMatrix(Location[] startLocations, Location[] destinationLocations) throws IOException {
        JsonArrayBuilder sourcesBuilder = Json.createArrayBuilder();
        JsonArrayBuilder targetsBuilder = Json.createArrayBuilder();
        for (Location source : startLocations) {
            sourcesBuilder.add(serializeLocation(source));
        }
        for (Location target : destinationLocations) {
            targetsBuilder.add(serializeLocation(target));
        }
        JsonObject requestJsonObject = Json.createObjectBuilder()
                .add("sources", sourcesBuilder.build())
                .add("targets", targetsBuilder.build())
                .add("costing", "pedestrian")
                .add("units", this.DistanceUnits.getApiString())
                .build();

        JsonObject response = this.getResponse(MatrixType.SOURCES_TO_TARGETS, requestJsonObject);
        //TODO check for error(s) in response
        String units = response.getString("units");
        JsonArray outerArray = response.getJsonArray(MatrixType.SOURCES_TO_TARGETS.getApiString());
        ArrayList<TimeMatrixEntry> out = new ArrayList<>();

        for (JsonValue innerJsonValue : outerArray) {
            JsonArray innerArray = (JsonArray)innerJsonValue;
            for (JsonValue value : innerArray) {
                JsonObject jsonObject = (JsonObject)value;
                TimeMatrixEntry entry = new TimeMatrixEntry(
                        jsonObject.getInt("from_index"),
                        jsonObject.getInt("to_index"),
                        jsonObject.getInt("time"),
                        jsonObject.getJsonNumber("distance").doubleValue(),
                        units
                );
                out.add(entry);
            }
        }

        return out;
    }

    private JsonObject serializeLocation(Location location) {
        return Json.createObjectBuilder()
                .add("lat", location.getLatitude())
                .add("lon", location.getLongitude())
                .build();
    }

    public List<TimeMatrixEntry> getWalkingMatrix(Location[] locations) throws IOException {
        JsonBuilder jsonBuilder = new JsonBuilder();
        jsonBuilder.addLocations(locations);
        JsonObject requestJsonObject = jsonBuilder.build(this.DistanceUnits);

        JsonObject response = this.getResponse(MatrixType.MANY_TO_MANY, requestJsonObject);
        //TODO check for error(s) in response
        String units = response.getString("units");

        ArrayList<TimeMatrixEntry> out = new ArrayList<>();

        for (JsonValue innerValue : response.getJsonArray(MatrixType.MANY_TO_MANY.getApiString())) {
            for (JsonValue element : ((JsonArray)innerValue)) {
                JsonObject jsonObject = (JsonObject)element;
                TimeMatrixEntry entry = new TimeMatrixEntry(
                        jsonObject.getInt("from_index"),
                        jsonObject.getInt("to_index"),
                        jsonObject.getInt("time"),
                        jsonObject.getJsonNumber("distance").doubleValue(),
                        units
                );
                out.add(entry);
            }
        }

        return out;
    }

    private class JsonBuilder {

        javax.json.JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        public JsonArrayBuilder addLocation(Location location) {
            return arrayBuilder.add(getJsonObject(location));
        }

        public JsonArrayBuilder addLocations(Location[] locations) {
            for (Location location : locations) {
                addLocation(location);
            }

            return arrayBuilder;
        }

        private JsonObject getJsonObject(Location location) {
            return Json.createObjectBuilder()
                    .add("lat", location.getLatitude())
                    .add("lon", location.getLongitude())
                    .build();
        }

        public JsonObject build(Units units) {
            builder.add("locations", arrayBuilder.build())
                    .add("costing", CostingModel.PEDESTRIAN.getApiString())
                    .add("units", units.getApiString());

            return builder.build();
        }
    }
}
