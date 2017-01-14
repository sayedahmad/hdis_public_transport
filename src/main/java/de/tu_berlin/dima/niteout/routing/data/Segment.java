package de.tu_berlin.dima.niteout.routing.data;

import java.time.LocalDate;

/**
 * Represents a single part of a route between two locations.
 */
public class Segment {

    private TransportMode mode;
    private Location startLocation;
    private Location destinationLocation;
    private LocalDate departureTime;
    private LocalDate arrivalTime;

    public TransportMode getMode() {
        return mode;
    }

    public void setMode(TransportMode mode) {
        this.mode = mode;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(Location destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public LocalDate getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDate departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDate getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDate arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
