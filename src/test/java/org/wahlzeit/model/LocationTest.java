package org.wahlzeit.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.*;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.wahlzeit.services.Session;
import org.wahlzeit.services.DatabaseConnection;
import org.wahlzeit.services.SessionManager;
import org.wahlzeit.services.SysSession;
import org.wahlzeit.services.SysConfig;

public class LocationTest {

    private static final double tolerance = 0.000001;
    private static Location testloc;
    private static Location testloc2;
    private static Location testloc3;
    private static Location testloc4;
    private static Location testloc5;
    private static Location testloc6;

    @BeforeClass
    public static void setupTests() {
        SysConfig.getInstance();

        boolean dbAvailable = DatabaseConnection.waitForDatabaseIsReady(30, 1000);
        if (!dbAvailable) {
            throw new RuntimeException("Unable to proceed with wahlzeit app. DB connection could not be established.");
        }

        Session mainSession = new SysSession("system");
        SessionManager.setThreadLocalSession(mainSession);
    }

    @Test
    public void testCoordinateBasicMethods() {
        CartesianCoordinate testco = new CartesianCoordinate(1.0, 4.0, 8.1);
        double[] exp = new double[3];
        exp[0] = 1.0;
        exp[1] = 4.0;
        exp[2] = 8.1;

        double[] act = new double[3];
        act[0] = testco.getXCoordinate();
        act[1] = testco.getYCoordinate();
        act[2] = testco.getZCoordinate();
        assertArrayEquals(exp, act, tolerance);
    }

    @Test
    public void testLocation() throws SQLException {
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

        CartesianCoordinate testco = new CartesianCoordinate(-1.0, 4.0, 8.1);
        testloc5 = new Location(testco);
        double[] exp = new double[3];
        exp[0] = -1.0;
        exp[1] = 4.0;
        exp[2] = 8.1;

        double[] act = new double[3];
        act[0] = testloc5.getCartesianCoordinate().getXCoordinate();
        act[1] = testloc5.getCartesianCoordinate().getYCoordinate();
        act[2] = testloc5.getCartesianCoordinate().getZCoordinate();
        assertArrayEquals(exp, act, tolerance);

        testloc6 = new Location(1.8976, -2.098765, 0.0);
        exp[0] = 1.8976;
        exp[1] = -2.098765;
        exp[2] = 0.0;

        act[0] = testloc6.getCartesianCoordinate().getXCoordinate();
        act[1] = testloc6.getCartesianCoordinate().getYCoordinate();
        act[2] = testloc6.getCartesianCoordinate().getZCoordinate();
        assertArrayEquals(exp, act, tolerance);

        testloc6.setCoordinate(testco);
        exp[0] = -1.0;
        exp[1] = 4.0;
        exp[2] = 8.1;

        act[0] = testloc6.getCartesianCoordinate().getXCoordinate();
        act[1] = testloc6.getCartesianCoordinate().getYCoordinate();
        act[2] = testloc6.getCartesianCoordinate().getZCoordinate();
        assertArrayEquals(exp, act, tolerance);

        try {
            testloc6.setCoordinate(null);
            fail("null isn't regcognized");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

    }

    public void testDatabaseLocation() throws SQLException{
        testloc4 = new Location(0.0, 1.3, 3.56);
        int loc_id = testloc4.getID();
        Statement st = LocationManager.getInstance().getStatement();
        String sqlQuery = "SELECT * FROM location WHERE location_id = " + loc_id;
        ResultSet rs = st.executeQuery(sqlQuery);
        if(!rs.next()){
            fail();
        }
        double x = rs.getDouble("x_coordinate");
        double y = rs.getDouble("y_coordinate");
        double z = rs.getDouble("z_coordinate");
        assertEquals(x, testloc4.getCartesianCoordinate().getXCoordinate(), tolerance);
        assertEquals(y, testloc4.getCartesianCoordinate().getYCoordinate(), tolerance);
        assertEquals(z, testloc4.getCartesianCoordinate().getZCoordinate(), tolerance);
        deleteLoc(testloc4.getID());

        //Test for setCoordinate
        CartesianCoordinate test_cord = new CartesianCoordinate(19.00, 04.05, 19.94);
        testloc4.setCoordinate(test_cord);
        Statement st2 = LocationManager.getInstance().getStatement();
        String sqlQuery2 = "SELECT * FROM location WHERE location_id = " + loc_id;
        ResultSet rs2 = st2.executeQuery(sqlQuery2);
        if(!rs2.next()){
            fail();
        }
        x = rs.getDouble("x_coordinate");
        y = rs.getDouble("y_coordinate");
        z = rs.getDouble("z_coordinate");
        assertEquals(x, testloc4.getCartesianCoordinate().getXCoordinate(), tolerance);
        assertEquals(y, testloc4.getCartesianCoordinate().getYCoordinate(), tolerance);
        assertEquals(z, testloc4.getCartesianCoordinate().getZCoordinate(), tolerance);

        //Test for getLocationFromID
        testloc3 = LocationManager.getInstance().getLocationFromID(loc_id);
        if(!test_cord.equals(testloc3.getCartesianCoordinate())){
            fail();
        }
        deleteLoc(testloc3.getID());
        
    }

    @Test
    public void testPhotoLocation() throws SQLException {
        CartesianCoordinate testco = new CartesianCoordinate(-1.0, -231.0, 8.1);
        testloc = new Location(testco);
        Photo testphoto = new Photo();
        testphoto.setLocation(testloc);
        double[] exp = new double[3];
        exp[0] = -1.0;
        exp[1] = -231.0;
        exp[2] = 8.1;

        double[] act = new double[3];
        act[0] = testphoto.getLocation().getCartesianCoordinate().getXCoordinate();
        act[1] = testphoto.getLocation().getCartesianCoordinate().getYCoordinate();
        act[2] = testphoto.getLocation().getCartesianCoordinate().getZCoordinate();
        assertArrayEquals(exp, act, tolerance);

        testloc2 = new Location(0.167, 1.875, 65.1900);
        Photo testphoto2 = new Photo();
        testphoto2.setLocation(testloc2);
        exp[0] = 0.1670;
        exp[1] = 1.875;
        exp[2] = 65.1900;

        act[0] = testphoto2.getLocation().getCartesianCoordinate().getXCoordinate();
        act[1] = testphoto2.getLocation().getCartesianCoordinate().getYCoordinate();
        act[2] = testphoto2.getLocation().getCartesianCoordinate().getZCoordinate();
        assertArrayEquals(exp, act, tolerance);
    }

    @Test
    public void testDistance() {
        CartesianCoordinate c1 = new CartesianCoordinate(2.0, 1.0, 4.0);
        try {
            c1.getCartesianDistance(null);
            fail("null isn't regcognized");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        CartesianCoordinate c2 = new CartesianCoordinate(2.0, 1.0, 4.0);
        assertEquals(0.0, c1.getCartesianDistance(c2), tolerance);

        CartesianCoordinate c3 = new CartesianCoordinate(0.0, 0.0, 0.0);
        CartesianCoordinate c4 = new CartesianCoordinate(0.0, 0.0, 0.0);
        assertEquals(0.0, c3.getCartesianDistance(c4), tolerance);

        CartesianCoordinate c5 = new CartesianCoordinate(-2.0, 1.0, 4.0);
        assertEquals(4.0, c1.getCartesianDistance(c5), tolerance);

        CartesianCoordinate c6 = new CartesianCoordinate(400.268, 267.978, 567.987);
        CartesianCoordinate c7 = new CartesianCoordinate(76.5879, 0.7856, 1.0976);
        assertEquals(705.3539378, c6.getCartesianDistance(c7), tolerance);

    }

    @Test
    public void testIsEqual() {
        
        CartesianCoordinate c1 = new CartesianCoordinate(2.0, 1.0, 4.0);
        try {
            c1.getCartesianDistance(null);
            fail("null isn't regcognized");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        CartesianCoordinate c2 = new CartesianCoordinate(2.0, 1.0, 4.0);
        assertTrue(c1.equals(c2));

        assertFalse(c1.equals(null));

        CartesianCoordinate c3 = new CartesianCoordinate(-2.0, 1.0, 4.0);
        assertFalse(c1.equals(c3));

        CartesianCoordinate c4 = new CartesianCoordinate(2.0, 1.00000001, 4.0);
        assertTrue(c4.equals(c1));
    }
    
    @AfterClass
    public static void clean() throws SQLException{
        deleteLoc(testloc.getID());
        deleteLoc(testloc2.getID());
        //deleteLoc(testloc3.getID());
        //deleteLoc(testloc4.getID());
        deleteLoc(testloc5.getID());
        deleteLoc(testloc6.getID());
        
    }

    public static void deleteLoc(int id) throws SQLException{
        Statement st = LocationManager.getInstance().getStatement();
        String sqlQuery = "Delete From location WHERE location_id = " + id;
        st.execute(sqlQuery);
    }
}
