package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Address;
import de.tu_berlin.dima.niteout.routing.model.Location;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by aardila on 2/15/17.
 */
public class GeocodingServiceTest {

    @Test
    public void getLocationTest() {

        Address address = new Address.AddressBuilder()
                .street("Julicher strasse")
                .houseNumber("1")
                .city("Berlin")
                .postalCode(13357)
                .build();

        GeocodingService service = new GeocodingService();
        Location fixture = null;

        try {
            fixture = service.getLocation(address);
        } catch (RoutingAPIException e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }

        Assert.assertNotNull(fixture);
        Assert.assertTrue(LocationDirectory.BERLIN_BOUNDING_BOX.contains(fixture));
    }
}
