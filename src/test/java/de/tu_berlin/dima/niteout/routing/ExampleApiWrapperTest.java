package de.tu_berlin.dima.niteout.routing;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Just a fake test to show how api key loading works
 */
public class ExampleApiWrapperTest {

    @Test
    public void getApiKey() throws Exception {
        Object key = System.getProperty("API_KEY_EXAMPLEAPI");
        assertNotNull(key);
        assertEquals(key, "abc123");
    }
}
