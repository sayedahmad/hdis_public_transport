package de.tu_berlin.dima.niteout.routing;

import com.google.common.util.concurrent.RateLimiter;
import de.tu_berlin.dima.niteout.routing.model.*;
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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static java.util.stream.Collectors.toList;

/*
 *
 *
 * API Wrapper that uses the HERE Api
 * www.here.com
 * Example API call:
 *
 * https://route.cit.api.here.com/routing/7.2/calculateroute.json
 *   ?app_id={YOUR_APP_ID}
 *   &app_code={YOUR_APP_CODE}
 *   &waypoint0=geo!52.530,13.326
 *   &waypoint1=geo!52.513,13.407
 *   &departure=now
 *   &mode=fastest;publicTransport
 *   &combineChange=true
 */
// TODO add more comments, document methods

/**
 * The Wrapper for the here.com API which wraps the requesting and network logic and just returns simple objects of our
 * model that our Service can work with, to keep the dependencies of this API only inside this class.
 */
class HereApiWrapper implements PublicTranportAPI {

    private final static String URL_MAIN = "https://route.cit.api.here.com/routing/7.2/calculateroute.json";
    private final static String URL_APP_ID = "app_id=%s";
    private final static String URL_APP_CODE = "app_code=%s";
    private final static String URL_START = "waypoint0=geo!%s,%s";
    private final static String URL_DESTINATION = "waypoint1=geo!%s,%s";
    private final static String URL_DEPARTURE = "departure=%s";
    private final static String URL_MODE = "mode=fastest;publicTransport";
    private final static String URL_COMBINE_CHANGE = "combineChange=true";
    private final static double MAX_API_RPS = 1;

    private final String apiId;
    private final String apiCode;

    private final RateLimiter rateLimiter = RateLimiter.create(MAX_API_RPS); // TODO fine-tune RPS value
    private final OkHttpClient httpClient = new OkHttpClient();

    public HereApiWrapper(String apiId, String apiCode) {
        if (apiId == null || apiId.trim().isEmpty() || apiCode == null || apiCode.trim().isEmpty()) {
            throw new IllegalArgumentException("apiId and apiCode cannot be null or empty");
        }
        this.apiId = apiId;
        this.apiCode = apiCode;
    }

    @Override
    public int getPublicTransportTripTime(Location start, Location destination, LocalDateTime departureTime) {
        return getMatrixEntryForRouteArguments(0, 0, start, destination, departureTime).getTime();
    }

    @Override
    public List<TimeMatrixEntry> getMultiModalMatrix(Location[] startLocations, Location[] destinationLocations,
                                                     LocalDateTime departureTime) {

        IntStream startIndices = IntStream.range(0, startLocations.length);

        // Parallelize all start locations, map each of them to all destination locations and get a MatrixEntry for
        // each combination. Then collect them again to a single list and return it.
        // TODO check parallelism, now it just depends on startDestinations and does not scale for 1-n
        return startIndices.parallel()
                .mapToObj(i -> IntStream.range(0, destinationLocations.length)
                        .mapToObj(j ->
                                getMatrixEntryForRouteArguments(i, j, startLocations[i], destinationLocations[j], departureTime))
                        .collect(toList()))
                .flatMap(l -> l.stream())
                .collect(toList());
    }

    private TimeMatrixEntry getMatrixEntryForRouteArguments(int fromIndex, int toIndex, Location start,
                                                            Location destination, LocalDateTime departure) {

        System.out.println(LocalDateTime.now() + ":: request  #" + toIndex + ":" + fromIndex);
        Reader response = null;
        try {
            response = getHTTPResponse(start, destination, departure);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assert response != null;
        JsonObject json = null;

        try {
            json = Json.createReader(response).readObject();
        } catch (JsonParsingException e) {
            System.out.println(LocalDateTime.now() + ":: fail     #" + fromIndex + ":" + toIndex + "\t " + e.getClass().getSimpleName());
        }

        assert json != null;
        return getTimeMatrixEntryFromJsonRoute(fromIndex, toIndex, json);
    }

    private TimeMatrixEntry getTimeMatrixEntryFromJsonRoute(int fromIndex, int toIndex, JsonObject json) {

        // only get first RouteSummary as it will not return alternatives due to missing 'alternative' request parameter
        JsonObject jsonRouteSummary = json
                .getJsonObject("response")
                .getJsonArray("route")
                .getJsonObject(0)
                .getJsonObject("summary");

        int distance = jsonRouteSummary.getInt("distance");
        int time = jsonRouteSummary.getInt("baseTime");
        
        return new TimeMatrixEntry(fromIndex, toIndex, time, distance, DistanceUnits.KILOMETERS);
    }

    @Override
    public RouteSummary getPublicTransportRouteSummary(Location start, Location destination, LocalDateTime departure) {
        Reader responseReader = null;
        try {
            responseReader = getHTTPResponse(start, destination, departure);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assert responseReader != null;
        JsonObject json = null;

        try {
            json = Json.createReader(responseReader).readObject();
        } catch (JsonParsingException e) {
            System.out.println(LocalDateTime.now() + ":: fail getPublicTransportDirections: " + e.getMessage());
        }

        assert json != null;
        return getRouteSummaryFromJsonResponse(json);

    }

    private RouteSummary getRouteSummaryFromJsonResponse(JsonObject json) {

        JsonObject route = null;
        try {
            route = json
                    .getJsonObject("response")
                    .getJsonArray("route")
                    .getJsonObject(0);
        } catch (NullPointerException e) {
            System.err.println("no route in:\n" + json.toString());
            throw new IllegalStateException("no route in response");
        }

        assert route != null;

        // travel times for modes: route/leg[]/maneuver{_type,traveltime}
        JsonArray legs = route.getJsonArray("leg");

        int publicTransportTravelTime = 0,
                walkingTravelTime = 0;

        for (int i = 0; i < legs.size(); i++) {
            JsonArray maneuvers = legs.getJsonObject(i).getJsonArray("maneuver");
            for (int j = 0; j < maneuvers.size(); j++) {
                JsonObject maneuver = maneuvers.getJsonObject(j);
                String type = maneuver.getJsonString("_type").getString();
                int travelTime = maneuver.getInt("travelTime");
                switch (type) {
                    case "PrivateTransportManeuverType":
                        walkingTravelTime += travelTime;
                        break;
                    case "PublicTransportManeuverType":
                        publicTransportTravelTime += travelTime;
                        break;
                    default:
                        throw new IllegalStateException("Cannot handle transport type: " + type);
                }
            }
        }

        HashMap<TransportMode, Integer> modeOfTransportTravelTimes = new HashMap<>();
        modeOfTransportTravelTimes.put(TransportMode.PUBLIC_TRANSPORT, publicTransportTravelTime);
        modeOfTransportTravelTimes.put(TransportMode.WALKING, walkingTravelTime);

        // total duration: route/summary/travelTime
        int duration = route.getJsonObject("summary").getInt("travelTime");

        // departure: route/summary/departure
        String departureAsISOString = route.getJsonObject("summary").getString("departure");
        LocalDateTime departure = LocalDateTime.parse(departureAsISOString, ISO_OFFSET_DATE_TIME);

        // arrival: departure + travelTime
        LocalDateTime arrival = departure.plus(Duration.ofSeconds(duration));

        // number of changes: route/publicTransportLine -1
        int numberOfChanges = route.getJsonArray("publicTransportLine").size() - 1;

        // distance: route/summary/distance
        int distance = route.getJsonObject("summary").getInt("distance");

        RouteSummary routeSummary = new RouteSummary();
        routeSummary.setArrivalTime(arrival);
        routeSummary.setDepartureTime(departure);
        routeSummary.setNumberOfChanges(numberOfChanges);
        routeSummary.setTotalDuration(duration);
        routeSummary.setModeOfTransportTravelTimes(modeOfTransportTravelTimes);
        routeSummary.setTotalDistance(distance);
        return routeSummary;

    }

    private Reader getHTTPResponse(Location start, Location destination, LocalDateTime departure) throws IOException {
        String url = buildURL(start, destination, departure);

        System.out.println("call URL " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        //Acquire a ticket from the rate limiter
        rateLimiter.acquire();
        Response response = httpClient.newCall(request).execute();
        
        return response.body().charStream();
    }

    private String buildURL(Location start, Location destination, LocalDateTime departure) {
        return URL_MAIN +
                formatFirstParameter(URL_APP_ID, apiId) +
                formatParameter(URL_APP_CODE, apiCode) +
                formatParameter(URL_START, start.getLatitude(), start.getLongitude()) +
                formatParameter(URL_DESTINATION, destination.getLatitude(), destination.getLongitude()) +
                formatParameter(URL_DEPARTURE, departure.format(DateTimeFormatters.ISO_LOCAL_DATE_TIME_NO_NANOSECONDS)) +
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
}
