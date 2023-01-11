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

public class SportTypeTest{

    private static Sport test_sport1;
    private static SportManager sportManager_instance;


    @BeforeClass
    public static void setupTests() {
        SysConfig.getInstance();

        boolean dbAvailable = DatabaseConnection.waitForDatabaseIsReady(30, 1000);
        if (!dbAvailable) {
            throw new RuntimeException("Unable to proceed with wahlzeit app. DB connection could not be established.");
        }

        Session mainSession = new SysSession("system");
        SessionManager.setThreadLocalSession(mainSession);
        sportManager_instance = SportManager.getInstance();
    }

    @Test
    public void testSportDatabaseBehaviour() throws SQLException{
        test_sport1 = sportManager_instance.createSport("Ballsport", "Fussball");
        int sport_id = test_sport1.getID();
        Statement st = sportManager_instance.getStatement();
        String sqlQuery = "SELECT * FROM sport WHERE id = " + sport_id;
        ResultSet rs = st.executeQuery(sqlQuery);
        if (!rs.next()) {
            fail();
        }
        String name = rs.getString("Name");
        assertEquals("Fussball", name);


    }

    @AfterClass
    public static void clean() throws SQLException {
        deleteSport(test_sport1.getID());

    }

    public static void deleteSport(int id) throws SQLException {
        Statement st = SportManager.getInstance().getStatement();
        String sqlQuery = "Delete From sport WHERE id = " + id;
        st.execute(sqlQuery);
    }
}