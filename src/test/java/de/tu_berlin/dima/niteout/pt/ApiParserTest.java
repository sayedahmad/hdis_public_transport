package de.tu_berlin.dima.niteout.pt;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for the ApiParser
 */
public class ApiParserTest {
    @Test
    public void runApiCall() throws Exception {
        ApiParser parser = new ApiParser();
        assertTrue(parser.runApiCall());
    }

}