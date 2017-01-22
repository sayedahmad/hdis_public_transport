package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.mapzen.CostingModel;
import de.tu_berlin.dima.niteout.routing.model.mapzen.MatrixType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import javax.json.*;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by aardila on 1/22/2017.
 */
public class MapzenMatrixApiWrapper {

    private final String urlFormat = "https://matrix.mapzen.com/%s?json=%s&api_key=%s";
    private final String apiKey;
    private OkHttpClient httpClient;

    public MapzenMatrixApiWrapper(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient();
    }

    private JsonObject getResponse(MatrixType matrixType, JsonObject jsonObject) throws IOException {
        String url = String.format(this.urlFormat, matrixType.getApiString(), jsonObject, this.apiKey);
        Request request = new Request.Builder().url(url).build();

        Response response = this.httpClient.newCall(request).execute();

        JsonReader jsonReader = Json.createReader(response.body().charStream());
        JsonObject responseJsonObject = jsonReader.readObject();

        return responseJsonObject;
    }
    public List<Pair<Location, Integer>> getWalkingMatrix(Location start, Location[] destinations) throws IOException {

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                    .add("lat", start.getLatitude())
                    .add("lon", start.getLongitude()));

        for (Location location : destinations) {
            arrayBuilder.add(Json.createObjectBuilder()
                .add("lat", location.getLatitude())
                .add("lon", location.getLongitude()));
        }

        JsonObject jsonObject = Json.createObjectBuilder()
                .add("locations", arrayBuilder.build())
                .add("costing", CostingModel.PEDESTRIAN.getApiString())
                .add("units", "km")
                .build();


        JsonObject response = this.getResponse(MatrixType.OneToMany, jsonObject);
        //TODO check for error(s) in response
        JsonArray outerArray = response.getJsonArray(MatrixType.OneToMany.getApiString());
        JsonArray innerAray = outerArray.getJsonArray(0);

        ArrayList<Pair<Location, Integer>> out = new ArrayList(destinations.length);

        for (JsonValue value : innerAray) {
            JsonObject jo = (JsonObject)value;
            int index = jo.getInt("to_index");
            if (index == 0) { continue; } // skip 'from_index' : 0 'to_index' : 0 since it's the departure/start point
            int seconds = jo.getInt("time");
            out.add(new Pair<Location, Integer>(destinations[index-1], seconds));
        }

        return out;
    }

    public void getWalkingMatrix(Location[] startLocations, Location destinationLocation) {

    }

    public void getWalkingMatrix(Location[] startLocations, Location[] destinationLocations) {

    }
}
