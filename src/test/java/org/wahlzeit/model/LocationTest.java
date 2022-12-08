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

    private static final double tolerance = 0.000002;
    private static Location testloc;
    private static Location testloc2;
    private static Location testloc3;
    private static Location testloc4;
    private static Location testloc5;
    private static Location testloc6;
    private static Location testloc7;
    private static Location testloc8;

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
    public void testCoordinateShared(){
        int count = AbstractCoordinate.getNumberCoordinateElements();
        CartesianCoordinate.getCartesianCoordinateObject(1.0, 1.0, 1.0);
        int count2 = AbstractCoordinate.getNumberCoordinateElements();
        assertEquals(count + 1, count2);
        CartesianCoordinate.getCartesianCoordinateObject(1.0, 1.0, 1.0);
        count = AbstractCoordinate.getNumberCoordinateElements();
        assertEquals(count2, count);



        count = AbstractCoordinate.getNumberCoordinateElements();
        SphericCoordinate.getSphericCoordinateObject(1.0, 1.0, 1.0);
        count2 = AbstractCoordinate.getNumberCoordinateElements();
        assertEquals(count + 1, count2);
        SphericCoordinate.getSphericCoordinateObject(1.0, 1.0, 1.0);
        count = AbstractCoordinate.getNumberCoordinateElements();
        assertEquals(count2, count);



        SphericCoordinate testco = SphericCoordinate.getSphericCoordinateObject(0.5*Math.PI, 0.5*Math.PI, 80000);
        CartesianCoordinate.getCartesianCoordinateObject(0, 80000, 0);
        count = AbstractCoordinate.getNumberCoordinateElements();
        testco.asCartesianCoordinate();
        count2 = AbstractCoordinate.getNumberCoordinateElements();
        assertEquals(count, count2);

        testco = SphericCoordinate.getSphericCoordinateObject(0.2*Math.PI, 0.6*Math.PI, 20000);
        count = AbstractCoordinate.getNumberCoordinateElements();
        CartesianCoordinate testco2 = CartesianCoordinate.getCartesianCoordinateObject(-3632.712640, 11180.339887, 16180.339887);
        count2 = AbstractCoordinate.getNumberCoordinateElements();
        assertEquals(count, count2);
        testco.asCartesianCoordinate();
        count = AbstractCoordinate.getNumberCoordinateElements();
        assertEquals(count2, count);
        count = AbstractCoordinate.getNumberCoordinateElements();
        testco2.asSphericCoordinate();
        count2 = AbstractCoordinate.getNumberCoordinateElements();
        assertEquals(count, count2);

        CartesianCoordinate car_cord = CartesianCoordinate.getCartesianCoordinateObject(-20.532, -56.222, 200002);
        count = AbstractCoordinate.getNumberCoordinateElements();
        testco = car_cord.asSphericCoordinate();
        count2 = AbstractCoordinate.getNumberCoordinateElements();
        assertEquals(count, count2);
        CartesianCoordinate car_cord2 = testco.asCartesianCoordinate();
        count2 = AbstractCoordinate.getNumberCoordinateElements();
        assertEquals(count, count2);
        assertEquals(car_cord.getYCoordinate(),car_cord2.getYCoordinate(),tolerance);
        assertEquals(car_cord.getYCoordinate(),car_cord2.getYCoordinate(),tolerance);
        assertEquals(car_cord.getZCoordinate(),car_cord2.getZCoordinate(),tolerance);
    }
    @Test
    public void testCoordinateBasicMethods() {
        CartesianCoordinate testco = CartesianCoordinate.getCartesianCoordinateObject(1.0, 4.0, 8.1);
        double[] exp = new double[3];
        exp[0] = 1.0;
        exp[1] = 4.0;
        exp[2] = 8.1;

        double[] act = new double[3];
        act[0] = testco.getXCoordinate();
        act[1] = testco.getYCoordinate();
        act[2] = testco.getZCoordinate();
        assertArrayEquals(exp, act, tolerance);

        SphericCoordinate testco2 = SphericCoordinate.getSphericCoordinateObject(0.5 * Math.PI, 0.5 * Math.PI, 80000);
        exp[0] = 0.5 * Math.PI;
        exp[1] = 0.5 * Math.PI;
        exp[2] = 80000;

        act[0] = testco2.getPhi();
        act[1] = testco2.getTheta();
        act[2] = testco2.getRadius();
        assertArrayEquals(exp, act, tolerance);

        exp[0] = 0;
        exp[1] = 80000;
        exp[2] = 0;
        CartesianCoordinate testco2_car = testco2.asCartesianCoordinate();
        act[0] = testco2_car.getXCoordinate();
        act[1] = testco2_car.getYCoordinate();
        act[2] = testco2_car.getZCoordinate();
        assertArrayEquals(exp, act, tolerance);

        testco2 = SphericCoordinate.getSphericCoordinateObject(0.2 * Math.PI, 0.6 * Math.PI, 20000);
        exp[0] = 0.2 * Math.PI;
        exp[1] = 0.6 * Math.PI;
        exp[2] = 20000;

        act[0] = testco2.getPhi();
        act[1] = testco2.getTheta();
        act[2] = testco2.getRadius();
        assertArrayEquals(exp, act, tolerance);

        exp[0] = -3632.712640;
        exp[1] = 11180.339887;
        exp[2] = 16180.339887;
        testco2_car = testco2.asCartesianCoordinate();
        act[0] = testco2_car.getXCoordinate();
        act[1] = testco2_car.getYCoordinate();
        act[2] = testco2_car.getZCoordinate();
        assertArrayEquals(exp, act, tolerance);

        SphericCoordinate testco_sphe = testco.asSphericCoordinate();
        exp[0] = 0.470841;
        exp[1] = 1.325817;
        exp[2] = 9.089004;

        act[0] = testco_sphe.getPhi();
        act[1] = testco_sphe.getTheta();
        act[2] = testco_sphe.getRadius();
        assertArrayEquals(exp, act, tolerance);

        testco = CartesianCoordinate.getCartesianCoordinateObject(0, 0, 0);
        testco_sphe = testco.asSphericCoordinate();
        assertTrue(testco.equals(testco_sphe));

        testco = CartesianCoordinate.getCartesianCoordinateObject(0, 1, 0);
        testco_sphe = testco.asSphericCoordinate();
        assertTrue(testco.equals(testco_sphe));

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

        CartesianCoordinate testco = CartesianCoordinate.getCartesianCoordinateObject(-1.0, 4.0, 8.1);
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

        SphericCoordinate testco_ph = SphericCoordinate.getSphericCoordinateObject(Math.PI, 0, 20000);
        testloc7 = new Location(testco_ph);

        exp[0] = 0;
        exp[1] = 0;
        exp[2] = -20000;

        act[0] = testloc7.getCartesianCoordinate().getXCoordinate();
        act[1] = testloc7.getCartesianCoordinate().getYCoordinate();
        act[2] = testloc7.getCartesianCoordinate().getZCoordinate();
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

    @Test
    public void testDatabaseLocation() throws SQLException {
        testloc4 = new Location(0.0, 1.3, 3.56);
        int loc_id = testloc4.getID();
        Statement st = LocationManager.getInstance().getStatement();
        String sqlQuery = "SELECT * FROM location WHERE location_id = " + loc_id;
        ResultSet rs = st.executeQuery(sqlQuery);
        if (!rs.next()) {
            fail();
        }
        double x = rs.getDouble("x_coordinate");
        double y = rs.getDouble("y_coordinate");
        double z = rs.getDouble("z_coordinate");
        assertEquals(testloc4.getCartesianCoordinate().getXCoordinate(), x, tolerance);
        assertEquals(testloc4.getCartesianCoordinate().getYCoordinate(), y, tolerance);
        assertEquals(testloc4.getCartesianCoordinate().getZCoordinate(), z, tolerance);

        SphericCoordinate cord_sph = SphericCoordinate.getSphericCoordinateObject(0, Math.PI, 10000);
        testloc8 = new Location(cord_sph);
        loc_id = testloc8.getID();
        st = LocationManager.getInstance().getStatement();
        sqlQuery = "SELECT * FROM location WHERE location_id = " + loc_id;
        rs = st.executeQuery(sqlQuery);
        if (!rs.next()) {
            fail();
        }
        x = rs.getDouble("x_coordinate");
        y = rs.getDouble("y_coordinate");
        z = rs.getDouble("z_coordinate");
        assertEquals(testloc8.getCartesianCoordinate().getXCoordinate(), x, tolerance);
        assertEquals(testloc8.getCartesianCoordinate().getYCoordinate(), y, tolerance);
        assertEquals(testloc8.getCartesianCoordinate().getZCoordinate(), z, tolerance);

        // setCoordinate with SphericCoordinate
        testloc4.setCoordinate(cord_sph);
        Statement st2 = LocationManager.getInstance().getStatement();
        String sqlQuery2 = "SELECT * FROM location WHERE location_id = " + loc_id;
        ResultSet rs2 = st2.executeQuery(sqlQuery2);
        if (!rs2.next()) {
            fail();
        }
        x = rs2.getDouble("x_coordinate");
        y = rs2.getDouble("y_coordinate");
        z = rs2.getDouble("z_coordinate");
        assertEquals(testloc4.getCartesianCoordinate().getXCoordinate(),x, tolerance);
        assertEquals(testloc4.getCartesianCoordinate().getYCoordinate(),y, tolerance);
        assertEquals(testloc4.getCartesianCoordinate().getZCoordinate(),z, tolerance);

        // Test for setCoordinate with CartesianCoordinate
        CartesianCoordinate test_cord = CartesianCoordinate.getCartesianCoordinateObject(19.00,04.05, 19.94);
        loc_id = testloc4.getID();
        testloc4.setCoordinate(test_cord);
        st2 = LocationManager.getInstance().getStatement();
        sqlQuery2 = "SELECT * FROM location WHERE location_id = " + loc_id;
        rs2 = st2.executeQuery(sqlQuery2);
        if (!rs2.next()) {
            fail();
        }
        x = rs2.getDouble("x_coordinate");
        y = rs2.getDouble("y_coordinate");
        z = rs2.getDouble("z_coordinate");
        assertEquals(testloc4.getCartesianCoordinate().getXCoordinate(), x, tolerance);
        assertEquals(testloc4.getCartesianCoordinate().getYCoordinate(), y, tolerance);
        assertEquals(testloc4.getCartesianCoordinate().getZCoordinate(), z, tolerance);

        

        // Test for getLocationFromID
        testloc3 = LocationManager.getInstance().getLocationFromID(loc_id);
        assertTrue(test_cord.equals(testloc3.getCartesianCoordinate()));
    }

    @Test
    public void testPhotoLocation() throws SQLException {
        CartesianCoordinate testco = CartesianCoordinate.getCartesianCoordinateObject(-1.0, -231.0, 8.1);
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
        // Test distance with CartesianCoordinate
        CartesianCoordinate c1 = CartesianCoordinate.getCartesianCoordinateObject(2.0, 1.0, 4.0);
        try {
            c1.getCartesianDistance(null);
            fail("null isn't regcognized");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        CartesianCoordinate c2 = CartesianCoordinate.getCartesianCoordinateObject(2.0, 1.0, 4.0);
        assertEquals(0.0, c1.getCartesianDistance(c2), tolerance);

        CartesianCoordinate c3 = CartesianCoordinate.getCartesianCoordinateObject(0.0, 0.0, 0.0);
        CartesianCoordinate c4 = CartesianCoordinate.getCartesianCoordinateObject(0.0, 0.0, 0.0);
        assertEquals(0.0, c3.getCartesianDistance(c4), tolerance);

        CartesianCoordinate c5 = CartesianCoordinate.getCartesianCoordinateObject(-2.0, 1.0, 4.0);
        assertEquals(4.0, c1.getCartesianDistance(c5), tolerance);

        CartesianCoordinate c6 = CartesianCoordinate.getCartesianCoordinateObject(400.268, 267.978, 567.987);
        CartesianCoordinate c7 = CartesianCoordinate.getCartesianCoordinateObject(76.5879, 0.7856, 1.0976);
        assertEquals(705.3539378, c6.getCartesianDistance(c7), tolerance);

        // Test distance with SphericCoordinate
        SphericCoordinate cs1 = SphericCoordinate.getSphericCoordinateObject(0, 1.5 * Math.PI, 14000);
        SphericCoordinate cs2 = SphericCoordinate.getSphericCoordinateObject(0, 1.5 * Math.PI, 14000);
        assertEquals(0.0, cs1.getCartesianDistance(cs2), tolerance);

        SphericCoordinate cs3 = SphericCoordinate.getSphericCoordinateObject(0.5 * Math.PI, 1.8 * Math.PI, 14000);
        assertEquals(19798.989873, cs3.getCartesianDistance(cs2), tolerance);
    }

    @Test
    public void testCentralAngle() {
        SphericCoordinate cs1 = SphericCoordinate.getSphericCoordinateObject(0, 1.5 * Math.PI, 14000);
        SphericCoordinate cs2 = SphericCoordinate.getSphericCoordinateObject(0, 1.5 * Math.PI, 14020);
        assertEquals(0.0, cs1.getCentralAngle(cs2), tolerance);

        cs2 = SphericCoordinate.getSphericCoordinateObject(0, 1.5 * Math.PI, 14200);
        try {
            assertEquals(0.0, cs1.getCentralAngle(cs2), tolerance);
            fail("different radius should fail");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        SphericCoordinate cs3 = SphericCoordinate.getSphericCoordinateObject(Math.PI, 1.25 * Math.PI, 14000);
        assertEquals(Math.PI, cs1.getCentralAngle(cs3), tolerance);
    }

    @Test
    public void testIsEqual() {

        CartesianCoordinate c1 = CartesianCoordinate.getCartesianCoordinateObject(2.0, 1.0, 4.0);
        try {
            c1.getCartesianDistance(null);
            fail("null isn't regcognized");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        CartesianCoordinate c2 = CartesianCoordinate.getCartesianCoordinateObject(2.0, 1.0, 4.0);
        assertTrue(c1.equals(c2));

        assertFalse(c1.equals(null));

        CartesianCoordinate c3 = CartesianCoordinate.getCartesianCoordinateObject(-2.0, 1.0, 4.0);
        assertFalse(c1.equals(c3));

        CartesianCoordinate c4 = CartesianCoordinate.getCartesianCoordinateObject(2.0, 1.00000001, 4.0);
        assertTrue(c4.equals(c1));

        SphericCoordinate cs1 = SphericCoordinate.getSphericCoordinateObject(0.5 * Math.PI, 0.5 * Math.PI, 10000);
        c2 = CartesianCoordinate.getCartesianCoordinateObject(0, 10000, 0);
        assertTrue(cs1.equals(c2));
        assertTrue(c2.equals(cs1));

        cs1 = SphericCoordinate.getSphericCoordinateObject(0.6 * Math.PI, 0.5 * Math.PI, 10000);
        assertFalse(cs1.equals(c2));

        cs1 = SphericCoordinate.getSphericCoordinateObject(0.2 * Math.PI, 0.6 * Math.PI, 20000);

        c2 = CartesianCoordinate.getCartesianCoordinateObject(-3632.712640, 11180.339887, 16180.339887);
        assertTrue(cs1.equals(c2));
        assertTrue(c2.equals(cs1));
    }

    @AfterClass
    public static void clean() throws SQLException {
        deleteLoc(testloc.getID());
        deleteLoc(testloc2.getID());
        deleteLoc(testloc3.getID());
        deleteLoc(testloc4.getID());
        deleteLoc(testloc5.getID());
        deleteLoc(testloc6.getID());
        deleteLoc(testloc7.getID());
        deleteLoc(testloc8.getID());

    }

    public static void deleteLoc(int id) throws SQLException {
        Statement st = LocationManager.getInstance().getStatement();
        String sqlQuery = "Delete From location WHERE location_id = " + id;
        st.execute(sqlQuery);
    }
}
