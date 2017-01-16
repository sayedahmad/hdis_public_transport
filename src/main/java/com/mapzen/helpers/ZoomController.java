package com.mapzen.helpers;

import com.mapzen.valhalla.Router;

import java.util.HashMap;

import static com.mapzen.valhalla.Router.Type.DRIVING;

public class ZoomController {
    public static final int DEFAULT_ZOOM = 17;
    public static final int DEFAULT_ZOOM_WALKING = 21;
    public static final int DEFAULT_ZOOM_BIKING = 19;
    public static final int DEFAULT_ZOOM_DRIVING = 17;

    public static final int DEFAULT_TURN_RADIUS = 50;
    public static final int DEFAULT_TURN_RADIUS_WALKING = 10;
    public static final int DEFAULT_TURN_RADIUS_BIKING = 20;
    public static final int DEFAULT_TURN_RADIUS_DRIVING = 50;

    private static final float ONE_METER_PER_SECOND_IN_MILES_PER_HOUR = 2.23694f;

    private int walkingZoom = DEFAULT_ZOOM_WALKING;
    private int bikingZoom = DEFAULT_ZOOM_BIKING;
    private int drivingZoom = DEFAULT_ZOOM_DRIVING;

    private int walkingTurnRadius = DEFAULT_TURN_RADIUS_WALKING;
    private int bikingTurnRadius = DEFAULT_TURN_RADIUS_BIKING;
    private int drivingTurnRadius = DEFAULT_TURN_RADIUS_DRIVING;

    private Router.Type transitMode = DRIVING;
    private DrivingSpeed currentDrivingSpeed = null;
    private DrivingSpeed averageDrivingSpeed = null;
    private HashMap<DrivingSpeed, Integer> zoomMap = new HashMap<DrivingSpeed, Integer>();
    private HashMap<DrivingSpeed, Integer> turnRadiusMap = new HashMap<DrivingSpeed, Integer>();

    public int getZoom() {
        switch (transitMode) {
            case WALKING:
                return walkingZoom;
            case BIKING:
                return bikingZoom;
            case DRIVING:
                return getZoomForCurrentDrivingSpeed();
            default:
                return DEFAULT_ZOOM;
        }
    }

    public int getTurnRadius() {
        switch (transitMode) {
            case WALKING:
                return walkingTurnRadius;
            case BIKING:
                return bikingTurnRadius;
            case DRIVING:
                return getTurnRadiusForCurrentDrivingSpeed();
            default:
                return DEFAULT_TURN_RADIUS;
        }
    }

    private int getZoomForCurrentDrivingSpeed() {
        Integer zoomLevelForCurrentSpeed = zoomMap.get(averageDrivingSpeed);
        if (zoomLevelForCurrentSpeed != null) {
            return zoomLevelForCurrentSpeed;
        }

        return drivingZoom;
    }

    private int getTurnRadiusForCurrentDrivingSpeed() {
        Integer turnRadiusForCurrentSpeed = turnRadiusMap.get(currentDrivingSpeed);
        if (turnRadiusForCurrentSpeed != null) {
            return turnRadiusForCurrentSpeed;
        }

        return drivingTurnRadius;
    }

    public void setTransitMode(Router.Type transitMode) {
        this.transitMode = transitMode;
    }

    public void setWalkingZoom(int walkingZoom) {
        this.walkingZoom = walkingZoom;
    }

    public void setBikingZoom(int bikingZoom) {
        this.bikingZoom = bikingZoom;
    }

    public void setDrivingZoom(int drivingZoom) {
        this.drivingZoom = drivingZoom;
    }

    public void setDrivingZoom(int zoom, DrivingSpeed speed) {
        zoomMap.put(speed, zoom);
    }

    public void setWalkingTurnRadius(int meters) {
        walkingTurnRadius = meters;
    }

    public void setBikingTurnRadius(int meters) {
        bikingTurnRadius = meters;
    }

    public void setDrivingTurnRadius(int meters) {
        drivingTurnRadius = meters;
    }

    public void setDrivingTurnRadius(int meters, DrivingSpeed speed) {
        turnRadiusMap.put(speed, meters);
    }

    public void setAverageSpeed(float metersPerSecond) {
        averageDrivingSpeed = getDrivingSpeed(metersPerSecond);
    }

    public void setCurrentSpeed(float metersPerSecond) {
        currentDrivingSpeed = getDrivingSpeed(metersPerSecond);
    }

    private DrivingSpeed getDrivingSpeed(float metersPerSecond) {
        if (metersPerSecond < 0) {
            throw new IllegalArgumentException("Speed less than zero is not permitted.");
        }

        float mph = metersPerSecondToMilesPerHour(metersPerSecond);
        if (mph < 15) {
            return DrivingSpeed.MPH_0_TO_15;
        } else if (mph < 25) {
            return DrivingSpeed.MPH_15_TO_25;
        } else if (mph < 35) {
            return DrivingSpeed.MPH_25_TO_35;
        } else if (mph < 50) {
            return DrivingSpeed.MPH_35_TO_50;
        } else {
            return DrivingSpeed.MPH_OVER_50;
        }
    }

    public static float metersPerSecondToMilesPerHour(float metersPerSecond) {
        return metersPerSecond * ONE_METER_PER_SECOND_IN_MILES_PER_HOUR;
    }

    public static float milesPerHourToMetersPerSecond(float milesPerHour) {
        return milesPerHour / ONE_METER_PER_SECOND_IN_MILES_PER_HOUR;
    }

    public enum DrivingSpeed {
        MPH_0_TO_15,
        MPH_15_TO_25,
        MPH_25_TO_35,
        MPH_35_TO_50,
        MPH_OVER_50
    }
}
