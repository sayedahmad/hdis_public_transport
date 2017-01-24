package de.tu_berlin.dima.niteout.routing.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Test class for {@link Location}.
 */
public class LocationTest {

    @Test
    public void test() {
    	 //TODO add tests
    	//Given
    	double latitude=13.32697;
    	double longitude=52.51221;
    	
    			Location locTest=new Location(latitude, longitude);
    			
    			//Then
    			//Assert.assertTrue(locTest.getLatitude().equals(latitude));
    			//Assert.assertTrue(locTest.getLongitude().equals(longitude));
    			assertEquals(locTest.getLatitude(),latitude, 0);
    			assertEquals(locTest.getLongitude(),longitude, 0);
    }
}
