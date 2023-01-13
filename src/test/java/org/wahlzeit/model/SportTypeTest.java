package org.wahlzeit.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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

    private static SportType test_sportType1;
    private static SportType test_sportType2;
    private static SportType test_sportType3;
    private static SportType test_sportType4;
    private static SportType test_sportType5;
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
    public void testSportDatabaseBehaviourWithoutAttributes() throws SQLException{
        //Test new SportType table and inserting into it
        test_sportType1 = sportManager_instance.createSportType("Ballsporttest");
        int sportType_id = test_sportType1.getID();
        Sport test_sport1 = sportManager_instance.createSport("Ballsporttest", "Fussballtest");

        Statement st = sportManager_instance.getStatement();
        String sqlQuery = "SELECT * FROM sporttypes WHERE id = " + sportType_id;
        ResultSet rs = st.executeQuery(sqlQuery);
        if (!rs.next()) {
            fail();
        }
        String st_name = rs.getString("name");
        assertEquals("Ballsporttest", st_name);

        String sport_id = test_sport1.getIdAsString();
        st = sportManager_instance.getStatement();
        sqlQuery = "SELECT * FROM " + st_name + "_sporttypes WHERE id = " + sport_id;
        rs = st.executeQuery(sqlQuery);
        if (!rs.next()) {
            fail();
        }
        String sp_name = rs.getString("name");
        assertEquals("Fussballtest", sp_name);

        //Test getSportFromID
        Sport sp_get = sportManager_instance.getSportFromID(test_sport1.getID(), test_sportType1.getID());
        assertEquals(test_sport1.getID(), sp_get.getID());
        try {
            sportManager_instance.getSportFromID(-1, 1);
            fail();
        } catch (IllegalArgumentException e) {

        }
        try {
            sportManager_instance.getSportFromID(1, -1);
            fail();
        } catch (IllegalArgumentException e) {

        }

        //Test getSportTypeFromID
        SportType st_get = sportManager_instance.getSportTypeFromID(test_sportType1.getID());
        assertEquals(test_sportType1.getID(), st_get.getID());
        try {
            sportManager_instance.getSportTypeFromID(-1);
            fail();
        } catch (IllegalArgumentException e) {

        }

        //Test getSportTypeFromName
        st_get = sportManager_instance.getSportTypeFromName(test_sportType1.getName());
        assertEquals(test_sportType1.getID(), st_get.getID());
        try {
            sportManager_instance.getSportTypeFromName("testfail");
            fail();
        } catch (IllegalArgumentException e) {

        }

        //Test update Sport
        test_sport1.setName("Handballtest");
        st = sportManager_instance.getStatement();
        sqlQuery = "SELECT * FROM " + st_name + "_sporttypes WHERE id = " + sport_id;
        rs = st.executeQuery(sqlQuery);
        if (!rs.next()) {
            fail();
        }
        String sp_name2 = rs.getString("name");
        assertEquals("Handballtest", sp_name2);


    }

    @Test
    public void testSportDatabaseBehaviourithAttributes() throws SQLException{
        //Test new SportType table and inserting into it
        test_sportType2 = sportManager_instance.createSportType("Denksporttest");
        test_sportType3 = sportManager_instance.createSportType("Brettspieltest", test_sportType2);
        String[] attr = new String[4];
        attr[0] = "Spieldauer";
        attr[1] = "Spieler1";
        attr[2] = "Spieler2";
        attr[3] = "Sieger";
        test_sportType4 = sportManager_instance.createSportType("Schachtest", test_sportType3, attr);

        int sportType_id2 = test_sportType2.getID();
        int sportType_id3 = test_sportType3.getID();
        int sportType_id4 = test_sportType4.getID();
        Sport test_sport2 = sportManager_instance.createSport("Schachtest", "Game1test");
        Statement st = sportManager_instance.getStatement();
        String sqlQuery = "SELECT * FROM sporttypes WHERE id = " + sportType_id2;
        ResultSet rs = st.executeQuery(sqlQuery);
        if (!rs.next()) {
            fail();
        }
        String st_name = rs.getString("name");
        assertEquals("Denksporttest", st_name);

        st = sportManager_instance.getStatement();
        sqlQuery = "SELECT * FROM sporttypes WHERE id = " + sportType_id3;
        rs = st.executeQuery(sqlQuery);
        if (!rs.next()) {
            fail();
        }
        st_name = rs.getString("name");
        int st_super = rs.getInt("superType");
        String expsubt = "4_";
        String subtypes = rs.getString("subtypes");
        assertEquals(expsubt, subtypes);
        assertEquals("Brettspieltest", st_name);
        assertEquals(test_sportType2.getID(), st_super);

        st = sportManager_instance.getStatement();
        sqlQuery = "SELECT * FROM sporttypes WHERE id = " + sportType_id4;
        rs = st.executeQuery(sqlQuery);
        if (!rs.next()) {
            fail();
        }
        st_name = rs.getString("name");
        st_super = rs.getInt("superType");
        String attrs = rs.getString("attributes");
        String subt = rs.getString("subtypes");
        String expattr = attr[0] + "_"+attr[1] + "_" + attr[2] + "_"+attr[3] + "_";
        assertEquals("Schachtest", st_name);
        assertEquals(test_sportType3.getID(), st_super);
        assertEquals(expattr, attrs);
        assertNull(subt);

        String sport_id = test_sport2.getIdAsString();
        st = sportManager_instance.getStatement();
        sqlQuery = "SELECT * FROM Schachtest_sporttypes WHERE id = " + sport_id;
        rs = st.executeQuery(sqlQuery);
        if (!rs.next()) {
            fail();
        }
        String sp_name = rs.getString("name");
        assertEquals("Game1test", sp_name);

        String[] attr2 = new String[4];
        attr2[0] = "24";
        attr2[1] = "TestSpieler1";
        attr2[2] = "TestSpieler2";
        attr2[3] = "TestSpieler1";
        Sport test_sport3 = sportManager_instance.createSport("Schachtest", "Game2test", attr2);
        st = sportManager_instance.getStatement();
        sqlQuery = "SELECT * FROM Schachtest_sporttypes WHERE id = " + test_sport3.getID();
        rs = st.executeQuery(sqlQuery);
        if (!rs.next()) {
            fail();
        }
        sp_name = rs.getString("name");
        assertEquals("Game2test", sp_name);

        //Test getSportFromID
        Sport sp_get = sportManager_instance.getSportFromID(test_sport2.getID(), test_sportType4.getID());
        assertEquals(test_sport2.getID(), sp_get.getID());

        //Test getSportTypeFromID
        SportType st_get = sportManager_instance.getSportTypeFromID(test_sportType2.getID());
        assertEquals(test_sportType2.getID(), st_get.getID());

        //Test getSportTypeFromName
        st_get = sportManager_instance.getSportTypeFromName(test_sportType2.getName());
        assertEquals(test_sportType2.getID(), st_get.getID());
    }

    @AfterClass
    public static void clean() throws SQLException {
        deleteSportType(test_sportType1);
        deleteSportType(test_sportType2);
        deleteSportType(test_sportType3);
        deleteSportType(test_sportType4);

    }

    public static void deleteSportType(SportType sty) throws SQLException {
        Statement st = sportManager_instance.getStatement();
        String sqlQuery = "DROP TABLE " +  sty.getName() + "_sporttypes";
        st.execute(sqlQuery);
        st = sportManager_instance.getStatement();
        sqlQuery = "DELETE FROM sporttypes WHERE id = " + sty.getID();
        st.execute(sqlQuery);
    }
}