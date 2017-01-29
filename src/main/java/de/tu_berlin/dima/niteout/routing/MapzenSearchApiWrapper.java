package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Address;
import de.tu_berlin.dima.niteout.routing.model.Location;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;

/**
 * Created by aardila on 1/28/2017.
 */
class MapzenSearchApiWrapper extends MapzenApi implements GeocodingAPI {

    private final String endpoint = "v1/search/structured";

    protected MapzenSearchApiWrapper(String apiKey) {
        super("search", apiKey);
    }

    @Override
    public Location getLocation(Address address) throws IOException {
        LinkedHashMap<String, String> queryString = new LinkedHashMap<>();
        queryString.put("address", address.getStreet() + " " + address.getHouseNumber());
        queryString.put("locality", address.getCity());
        queryString.put("postalcode", address.getPostalCode());
        queryString.put("country", "DE");
        queryString.put("size", "1");
        queryString.put("api_key", super.apiKey);

        JsonObject responseJson = null;
        try {
            responseJson = super.getResponse(endpoint, queryString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            //TODO
        }
        JsonArray features = responseJson.getJsonArray("features");

        Location location = null;

        if (features.size() > 0) {
            JsonObject feature = (JsonObject) features.get(0);
            JsonArray coordinates = feature.getJsonObject("geometry").getJsonArray("coordinates");
            location = new Location(
                    coordinates.getJsonNumber(1).doubleValue(),
                    coordinates.getJsonNumber(0).doubleValue());
        }

        return location;
    }
}
