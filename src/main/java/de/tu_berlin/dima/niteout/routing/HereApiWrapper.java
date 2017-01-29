package de.tu_berlin.dima.niteout.routing;

import com.google.common.util.concurrent.RateLimiter;
import de.tu_berlin.dima.niteout.routing.model.*;
import de.tu_berlin.dima.niteout.routing.model.mapzen.Units;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonParsingException;
import java.io.IOException;
import java.io.Reader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
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
    private final static double MAX_API_RPS = 1.0;

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

    final RateLimiter rateLimiter = RateLimiter.create(MAX_API_RPS); // TODO play with RPS value

    public int getPublicTransportTripTime(Location start, Location destination, LocalDateTime departure) {
        return getMatrixEntryForRouteArguments(0,0,start,destination,departure).getTime();
    }

    public Route getPublicTransportDirections(Location start, Location destination, LocalDateTime departure) {
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
            System.out.println(LocalDateTime.now() + ":: fail getPublicTransportDirections: " + e.getMessage());
        }

        assert json != null;

        Route route = new Route();

        // TODO does this have to be implemented?
        //route.setSegments(json);

        return route;
    }

    public List<TimeMatrixEntry> getMultiModalMatrix(Location[] startLocations, Location[] destinationLocations,
                                                     LocalDateTime departureTime) {

        IntStream startIndices = IntStream.range(0, startLocations.length - 1);

        // Parallelize all start locations, map each of them to all destination locations and get a MatrixEntry for
        // each combination. Then collect them again to a single list and return it.
        // TODO check parallelism, now it just depends on startDestinations and does not scale for 1-n
        return startIndices.parallel()
                .mapToObj(i -> IntStream.range(0, destinationLocations.length - 1)
                        .mapToObj(j ->
                                getMatrixEntryForRouteArguments(i, j, startLocations[i], destinationLocations[j], departureTime))
                        .collect(toList()))
                .flatMap(l -> l.stream())
                .collect(toList());
    }

    private TimeMatrixEntry getMatrixEntryForRouteArguments(int fromIndex, int toIndex, Location start, Location destination,
                                                            LocalDateTime departure) {

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
        return getTimeMatrixEntryFromJsonRoute(fromIndex, toIndex, json);
    }

    private TimeMatrixEntry getTimeMatrixEntryFromJsonRoute(int fromIndex, int toIndex, JsonObject json) {
        assert json != null;
        // only get first RouteSummary as it will not return alternatives TODO is this really the case?
        JsonObject jsonRouteSummary = json
                .getJsonObject("response")
                .getJsonArray("route")
                .getJsonObject(0)
                .getJsonObject("summary");

        int distance = jsonRouteSummary.getInt("distance");
        int time = jsonRouteSummary.getInt("baseTime");
        final String units = Units.KM.getApiString();

        return new TimeMatrixEntry(fromIndex, toIndex, time, distance, units);
    }



    private Reader getHTTPResponse(Location start, Location destination, LocalDateTime departure) throws IOException {
        String url = buildURL(start, destination, departure.withNano(0));

        System.out.println("call URL " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        rateLimiter.acquire();
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
        return (firstParameter ? "?" : "&") + String.format(parameterTemplate, args);
    }

    // lazy init
    private OkHttpClient getHTTPClient() {
        if (httpClient == null) {
            this.httpClient = new OkHttpClient();
        }
        return httpClient;
    }
}
