package org.wahlzeit.model;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.wahlzeit.services.*;

import java.sql.*;

public class SportPhotoTest{

    private static final double tolerance = 0.000001;
    private static final LocationManager loc_man = LocationManager.getInstance();
    private static User test_user;
    private static SportPhoto test1;

    @BeforeClass
    public static void setupTests() {
        SysConfig.getInstance();

        boolean dbAvailable = DatabaseConnection.waitForDatabaseIsReady(30, 1000);
        if (!dbAvailable) {
            throw new RuntimeException("Unable to proceed with wahlzeit app. DB connection could not be established.");
        }

        Session mainSession = new SysSession("system");
        SessionManager.setThreadLocalSession(mainSession);


        test_user = new User("test2","test1234","testwahlzeit@wahlzeittest.fau", 0);
    }


    @Test
    public void testAddPhoto() throws SQLException{
        UserManager.getInstance().addUser(test_user);
        UserManager.getInstance().saveUser(test_user);
        test1 = new SportPhoto();
        Location loc = new Location(0.0, 1.3, 3.56);
        test_user.addPhoto(test1);
        SportPhotoManager.getInstance().addPhoto(test1);
        test1.setLocation(loc);
        SportPhotoManager.getInstance().savePhoto(test1);
        int id = test1.getId().asInt();
        Statement st = loc_man.getStatement();
        String sqlQuery = "SELECT * FROM photos WHERE id=" + id;
        ResultSet rs = st.executeQuery(sqlQuery);
        if(!rs.next()){
            fail();
        }
        int loc_id = rs.getInt("location_id");
        Statement st2 = loc_man.getStatement();
        String sqlQuery2 = "SELECT * FROM location WHERE location_id = " + loc_id;
        ResultSet rs2 = st2.executeQuery(sqlQuery2);
        if(!rs2.next()){
            fail();
        }
        double x = rs2.getDouble("x_coordinate");
        double y = rs2.getDouble("y_coordinate");
        double z = rs2.getDouble("z_coordinate");
        assertEquals(x, loc.getCartesianCoordinate().getXCoordinate(), tolerance);
        assertEquals(y, loc.getCartesianCoordinate().getYCoordinate(), tolerance);
        assertEquals(z, loc.getCartesianCoordinate().getZCoordinate(), tolerance);
    }

    @AfterClass
    public static void clean() throws SQLException{
        test_user.removePhoto(test1);
        Statement st = loc_man.getStatement();
        String sqlQuery = "Delete From location WHERE location_id = " + test1.getLocation().getID();
        st.execute(sqlQuery);
        Statement st2 = loc_man.getStatement();
        String sqlQuery2 = "Delete From photos WHERE id = " + test1.getId().asInt();
        st2.execute(sqlQuery2);
        UserManager.getInstance().deleteUser(test_user);
    }
}