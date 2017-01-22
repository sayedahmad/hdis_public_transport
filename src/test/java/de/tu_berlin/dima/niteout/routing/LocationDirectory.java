package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.BoundingBox;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by aardila on 1/22/2017.
 */
public final class LocationDirectory {



    private LocationDirectory() { }

    private static final BoundingBox BERLIN_BBOX =
            new BoundingBox(13.0904186037, 52.3685305255, 13.739978, 52.654269);

    public static final Location TU_BERLIN = new Location(52.51221, 13.32697);
    public static final Location BERLIN_HBF = new Location(52.524742, 13.369563);
    public static final Location BRANDENBURGER_TOR = new Location(52.516289,13.377729);
    public static final Location POTSDAMER_PLATZ = new Location(52.509498,13.376598);
    public static final Location SIEGESSÄULE = new Location(52.51458, 13.35015);

    public static Location getRandomLocationInBerlin() {
        return new Location(
            ThreadLocalRandom.current().nextDouble(BERLIN_BBOX.MinY, BERLIN_BBOX.MaxY),
            ThreadLocalRandom.current().nextDouble(BERLIN_BBOX.MinX, BERLIN_BBOX.MaxX)
        );
    }
}
