package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.Route;
import de.tu_berlin.dima.niteout.routing.model.TimeMatrixEntry;
import de.tu_berlin.dima.niteout.routing.model.mapzen.Units;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.validator.routines.UrlValidator;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParsingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/*
 * API Wrapper that uses the HERE Api
 * www.here.com
 * Example API call
 * <p>
 * https://route.cit.api.here.com/routing/7.2/calculateroute.json
 * ?app_id={YOUR_APP_ID}
 * &app_code={YOUR_APP_CODE}
 * &waypoint0=geo!52.530,13.326
 * &waypoint1=geo!52.513,13.407
 * &departure=now
 * &mode=fastest;publicTransport
 * &combineChange=true
 */

/**
 * The Wrapper for the here.com API which wraps the requesting and network logic and just returns simple objects of our
 * model that our Service can work with, to keep the dependencies of this API only inside this class.
 */
public class HereApiWrapper {

    private final static String URL_MAIN = "https://route.cit.api.here.com/routing/7.2/calculateroute.json";
    private final static String URL_APP_ID = "app_id=%s";
    private final static String URL_APP_CODE = "app_code=%s";
    private final static String URL_START = "waypoint0=geo!%s,%s";
    private final static String URL_DESTINATION = "waypoint1=geo!%s,%s";
    private final static String URL_DEPARTURE = "departure=%s";
    private final static String URL_MODE = "mode=fastest;publicTransport";
    private final static String URL_COMBINE_CHANGE = "combineChange=true";

    private final String apiId;
    private final String apiCode;

    private OkHttpClient httpClient;

    public HereApiWrapper(String apiId, String apiCode) {
        if (apiId == null || apiId.trim().isEmpty() || apiCode == null || apiCode.trim().isEmpty()) {
            throw new IllegalArgumentException("apiId and apiCode cannot be null or empty");
        }
        this.apiId = apiId;
        this.apiCode = apiCode;
    }

    public int getPublicTransportTripTime(Location start, Location destination, LocalDateTime departure) {

        Reader responseReader = null;
        try {
            responseReader = getHTTPResponse(start, destination, departure);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonObject json = null;

        try {
            json = Json.createReader(responseReader).readObject();
        } catch (JsonParsingException e) {
            System.out.println(LocalDateTime.now() + ":: getTime fail     #" + start + ":" + destination + "\t " +
                    e.getMessage());
        }

        assert json != null;
        // only get first RouteSummary as it will not return alternatives TODO is this really the case?
        JsonObject routeSummary = json
                .getJsonObject("response")
                .getJsonArray("route")
                .getJsonObject(0)
                .getJsonObject("summary");

        int distance = routeSummary.getInt("distance");
        int time = routeSummary.getInt("baseTime");

        return time;
    }

    public Route getPublicTransportDirections(Location start, Location destination, LocalDateTime startTime) {
       throw new UnsupportedOperationException("Not yet implemented");
    }

    public List<TimeMatrixEntry> getMultiModalMatrix(Location[] startLocations, Location[] destinationLocations,
                                                     LocalDateTime departure) {

        IntStream startIndices = IntStream.range(0, startLocations.length);

        return startIndices.parallel().mapToObj(i -> IntStream.range(0, destinationLocations.length).parallel().mapToObj(j ->
                    getMatrixEntry(i, j, startLocations[i], destinationLocations[j], departure))
                    .collect(toList()))
                .flatMap(Collection::stream).collect(toList());
    }

    private TimeMatrixEntry getMatrixEntry(int fromIndex, int toIndex, Location start, Location destination,
                                           LocalDateTime departure) {

        final String units = Units.KM.getApiString();

        System.out.println(LocalDateTime.now() + ":: request  #" + toIndex + ":" + fromIndex);
        Reader response = null;
        try {
            response = getHTTPResponse(start, destination, departure);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JsonObject json = null;

        try {
            json = Json.createReader(response).readObject();
        } catch (JsonParsingException e) {
            System.out.println(LocalDateTime.now() + ":: fail     #" + fromIndex + ":" + toIndex + "\t " + e.getClass().getSimpleName());
        }

        assert json != null;
        // only get first RouteSummary as it will not return alternatives TODO is this really the case?
        JsonObject routeSummary = json
                .getJsonObject("response")
                .getJsonArray("route")
                .getJsonObject(0)
                .getJsonObject("summary");

        int distance = routeSummary.getInt("distance");
        int time = routeSummary.getInt("baseTime");

        return new TimeMatrixEntry(fromIndex, toIndex, time, distance, units);
    }

    private Reader getHTTPResponse(Location start, Location destination, LocalDateTime departure) throws IOException {
        String url = buildURL(start, destination, departure.withNano(0));

        System.out.println("call URL " + url);

        UrlValidator urlValidator = new UrlValidator();
        assert urlValidator.isValid(url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = getHTTPClient().newCall(request).execute();
            return response.body().charStream();
    }

    private String buildURL(Location start, Location destination, LocalDateTime departure) {
        return URL_MAIN +
                formatFirstParameter(URL_APP_ID, apiId) +
                formatParameter(URL_APP_CODE, apiCode) +
                formatParameter(URL_START, start.getLatitude(), start.getLongitude()) +
                formatParameter(URL_DESTINATION, destination.getLatitude(), destination.getLongitude()) +
                formatParameter(URL_DEPARTURE, departure.toString()) +
                formatParameter(URL_MODE) +
                formatParameter(URL_COMBINE_CHANGE);
    }

    private static String formatParameter(String parameterTemplate, Object... args) {
        return formatParameter(false, parameterTemplate, args);
    }

    private static String formatFirstParameter(String parameterTemplate, Object... args) {
        return formatParameter(true, parameterTemplate, args);
    }

    private static String formatParameter(boolean firstParameter, String parameterTemplate, Object... args) {
        if (firstParameter) {
            return "?" + String.format(parameterTemplate, args);
        }
        return "&" + String.format(parameterTemplate, args);
    }

    private OkHttpClient getHTTPClient() {
        if (httpClient == null) {
            this.httpClient = new OkHttpClient();
        }
        return httpClient;
    }

    private String readerToString(Reader reader) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(reader);
            while ((line = br.readLine()) != null) {
                sb.append(line).append("add");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // always true
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
