package de.tu_berlin.dima.niteout.pt;


import de.tu_berlin.dima.niteout.pt.model.Location;
import de.tu_berlin.dima.niteout.pt.model.mapzen.CostingModel;

import java.io.*;
import java.net.*;

public class MapzenApiWrapper {

    private String apiKey;
    private String uriFormat = "https://valhalla.mapzen.com/route?json={%s}&api_key=";

    public MapzenApiWrapper(String apiKey) {

        if (apiKey == null || apiKey.trim().length() == 0)
            throw new IllegalArgumentException("apiKey cannot be null or empty");

        this.apiKey = apiKey;
        this.uriFormat += apiKey;
    }

    public String getRouteResponse(Location start, Location destination, CostingModel costingModel) throws MalformedURLException, IOException {

        String json =   "\"locations\":[" +
                        "{\"lat\":" + start.getLatitude()  + ",\"lon\":" + start.getLongitude() + "}," +
                        "{\"lat\":" + destination.getLatitude() + ",\"lon\":" + destination.getLongitude() + "}]," +
                        "\"costing\":\"" + costingModel.getApiString() + "\"";

        String uri = String.format(this.uriFormat, json);

//        try {
            URL url = new URL(uri);
            URLConnection connection = url.openConnection();
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String line;

            while ((line = in.readLine()) != null) {
                stringBuffer.append(line);
            }

            in.close();

            return stringBuffer.toString();
//        }
//        catch (MalformedURLException e) {
//            // new URL() failed
//            // ...
//        }
//        catch (IOException e) {
//            // openConnection() failed
//            // ...
//        }

    }
}
