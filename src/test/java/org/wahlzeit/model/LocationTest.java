package org.wahlzeit.model;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;



public class LocationTest {
    
    private static final double tolerance = 0.05;

    @Test
    public void testCoordinate(){
        Coordinate testco = new Coordinate(1.0, 4.0, 8.1);
        double[] exp = new double[3];
        exp[0] = 1.0;
        exp[1] = 4.0;
        exp[2] = 8.1;
        assertArrayEquals(exp, testco.getCoordinates(), tolerance);
    }
    @Test
    public void testLocation(){
        Coordinate testco = new Coordinate(-1.0, 4.0, 8.1);
        Location testloc = new Location(testco);
        double[] exp = new double[3];
        exp[0] = -1.0;
        exp[1] = 4.0;
        exp[2] = 8.1;
        assertArrayEquals(exp, testloc.cord.getCoordinates(), tolerance);
    }
    @Test
    public void testPhotoLocation(){
        Coordinate testco = new Coordinate(-1.0, -231.0, 8.1);
        Location testloc = new Location(testco);
        Photo testphoto = new Photo();
        testphoto.location = testloc;
        double[] exp = new double[3];
        exp[0] = -1.0;
        exp[1] = -231.0;
        exp[2] = 8.1;
        assertArrayEquals(exp, testphoto.location.cord.getCoordinates(), tolerance);
    }
}
