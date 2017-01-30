package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.DistanceUnits;
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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by aardila on 1/22/2017.
 */
class MapzenMatrixApiWrapper extends MapzenApi {

    private final Units MapzenDistanceUnits = Units.KM;
    private final DistanceUnits MatrixDistanceUnits = DistanceUnits.KILOMETERS;

    public MapzenMatrixApiWrapper(String apiKey) {

        super("matrix", apiKey);

        if (apiKey == null || apiKey.trim().length() == 0)
            throw new IllegalArgumentException("apiKey cannot be null or empty");
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
        JsonObject requestJsonObject = jsonBuilder.build(this.MapzenDistanceUnits);

        JsonObject response = null;
        try {
            response = super.getResponse(MatrixType.ONE_TO_MANY.getApiString(), requestJsonObject);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            //TODO
        }
        //TODO check for error(s) in response

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
                    MatrixDistanceUnits
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
        JsonObject requestJsonObject = jsonBuilder.build(this.MapzenDistanceUnits);

        JsonObject response = null;
        try {
            response = this.getResponse(MatrixType.MANY_TO_ONE.getApiString(), requestJsonObject);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            //TODO
        }
        //TODO check for error(s) in response
        
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
                    MatrixDistanceUnits
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
                .add("units", this.MapzenDistanceUnits.getApiString())
                .build();

        JsonObject response = null;
        try {
            response = this.getResponse(MatrixType.SOURCES_TO_TARGETS.getApiString(), requestJsonObject);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            //TODO
        }
        //TODO check for error(s) in response
        
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
                        MatrixDistanceUnits
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
        JsonObject requestJsonObject = jsonBuilder.build(this.MapzenDistanceUnits);

        JsonObject response = null;
        try {
            response = this.getResponse(MatrixType.MANY_TO_MANY.getApiString(), requestJsonObject);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            //TODO
        }

        //TODO check for error(s) in response
        
        ArrayList<TimeMatrixEntry> out = new ArrayList<>();

        for (JsonValue innerValue : response.getJsonArray(MatrixType.MANY_TO_MANY.getApiString())) {
            for (JsonValue element : ((JsonArray)innerValue)) {
                JsonObject jsonObject = (JsonObject)element;
                TimeMatrixEntry entry = new TimeMatrixEntry(
                        jsonObject.getInt("from_index"),
                        jsonObject.getInt("to_index"),
                        jsonObject.getInt("time"),
                        jsonObject.getJsonNumber("distance").doubleValue(),
                        MatrixDistanceUnits
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
