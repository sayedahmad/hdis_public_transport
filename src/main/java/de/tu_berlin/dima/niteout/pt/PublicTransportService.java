package de.tu_berlin.dima.niteout.pt;

/**
 * Service, that processes Public Transport requests.
 *
 * This class typically takes input of locations and times and returns options of different rides using the public
 * transportation in Berlin.
 *
 * It uses the Google Maps Directions API to acquire necessary information.
 */
public class PublicTransportService {

    public String getEmptyRoute() {
        return "No route found";
    }
}
