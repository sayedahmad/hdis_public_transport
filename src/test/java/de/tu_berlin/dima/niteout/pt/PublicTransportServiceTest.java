package de.tu_berlin.dima.niteout.pt;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for {@link PublicTransportService}.
 */
public class PublicTransportServiceTest {

    @Test
    public void getEmptyRoute() throws Exception {
        PublicTransportService service = new PublicTransportService();
        assertNotNull(service.getEmptyRoute());
        assertEquals("No route found", service.getEmptyRoute());
    }

}