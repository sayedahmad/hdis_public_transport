package de.tu_berlin.dima.niteout.pt.data;

import java.util.List;

public class Route {

    private List<Segment> segments;
    private TransportMode mode;
    private int duration;

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
        invalidate();
    }

    public TransportMode getMode() {
        return mode;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private void invalidate() {
        boolean hasPublicTransport = segments.stream().anyMatch(
                segment -> segment.getMode().equals(TransportMode.PUBLIC_TRANSPORT));
        mode = hasPublicTransport ? TransportMode.PUBLIC_TRANSPORT : TransportMode.WALKING;
    }
}
