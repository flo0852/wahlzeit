package org.wahlzeit.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.ResultSet;
import org.junit.Test;



public class LocationTest {
    
    private static final double tolerance = 0.000001;

    @Test
    public void testCoordinateBasicMethods(){
        Coordinate testco = new Coordinate(1.0, 4.0, 8.1);
        double[] exp = new double[3];
        exp[0] = 1.0;
        exp[1] = 4.0;
        exp[2] = 8.1;
        assertArrayEquals(exp, testco.getCoordinates(), tolerance);
    }
    @Test
    public void testLocation() throws SQLException{
        try {
            ResultSet rs = null;
            Location testnullloc = new Location(rs);
            fail("null isn't regcognized");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        try {
            Coordinate c = null;
            Location testnullloc = new Location(c);
            fail("null isn't regcognized");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        Coordinate testco = new Coordinate(-1.0, 4.0, 8.1);
        Location testloc = new Location(testco);
        double[] exp = new double[3];
        exp[0] = -1.0;
        exp[1] = 4.0;
        exp[2] = 8.1;
        assertArrayEquals(exp, testloc.getCoordinate().getCoordinates(), tolerance);

        Location testloc2 = new Location(1.8976, -2.098765, 0.0);
        exp[0] = 1.8976;
        exp[1] = -2.098765;
        exp[2] = 0.0;
        assertArrayEquals(exp, testloc2.getCoordinate().getCoordinates(), tolerance);

        testloc2.setCoordinate(testco);
        exp[0] = -1.0;
        exp[1] = 4.0;
        exp[2] = 8.1;
        assertArrayEquals(exp, testloc2.getCoordinate().getCoordinates(), tolerance);

        try {
            testloc2.setCoordinate(null);
            fail("null isn't regcognized");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

    }

    @Test
    public void testPhotoLocation() throws SQLException{ 
        Coordinate testco = new Coordinate(-1.0, -231.0, 8.1);
        Location testloc = new Location(testco);
        Photo testphoto = new Photo();
        testphoto.location = testloc;
        double[] exp = new double[3];
        exp[0] = -1.0;
        exp[1] = -231.0;
        exp[2] = 8.1;
        assertArrayEquals(exp, testphoto.location.getCoordinate().getCoordinates(), tolerance);

        Location testloc2 = new Location(0.167,1.875,65.1900);
        Photo testphoto2 = new Photo();
        testphoto2.location = testloc2;
        exp[0] = 0.1670;
        exp[1] = 1.875;
        exp[2] = 65.1900;
        assertArrayEquals(exp, testphoto2.location.getCoordinate().getCoordinates(), tolerance);
    }

    @Test
    public void testDistance(){
        Coordinate c1 = new Coordinate(2.0, 1.0, 4.0);
        try {
            c1.getDistance(null);
            fail("null isn't regcognized");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        Coordinate c2 = new Coordinate(2.0, 1.0, 4.0);
        assertEquals(0.0, c1.getDistance(c2), tolerance);

        Coordinate c3 = new Coordinate(0.0, 0.0, 0.0);
        Coordinate c4 = new Coordinate(0.0, 0.0, 0.0);
        assertEquals(0.0, c3.getDistance(c4), tolerance);

        Coordinate c5 = new Coordinate(-2.0, 1.0, 4.0);
        assertEquals(4.0, c1.getDistance(c5), tolerance);

        Coordinate c6 = new Coordinate(400.268, 267.978, 567.987);
        Coordinate c7 = new Coordinate(76.5879, 0.7856, 1.0976);
        assertEquals(705.3539378, c6.getDistance(c7), tolerance);


    }

    @Test
    public void testIsEqual(){
        Coordinate c1 = new Coordinate(2.0, 1.0, 4.0);
        try {
            c1.getDistance(null);
            fail("null isn't regcognized");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        Coordinate c2 = new Coordinate(2.0, 1.0, 4.0);
        assertTrue(c1.equals(c2));

        Coordinate c3 = new Coordinate(-2.0, 1.0, 4.0);
        assertFalse(c1.equals(c3));

        Coordinate c4 = new Coordinate(2.0, 1.00000001, 4.0);
        assertFalse(c4.equals(c1));
    }
}
