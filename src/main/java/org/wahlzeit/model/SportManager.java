package org.wahlzeit.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import java.util.*;

import org.wahlzeit.services.DatabaseConnection;
import org.wahlzeit.services.ObjectManager;
import org.wahlzeit.services.Persistent;
import org.wahlzeit.services.SysLog;

public class SportManager extends ObjectManager {
    private static final SportManager instance = new SportManager();

    private Set<SportType> rootTypes = new HashSet<SportType>();
    private Map<Integer, Sport> sports = new HashMap<Integer, Sport>();

    protected Map<Integer, Sport> unsavedSports = new HashMap<Integer, Sport>();

    private int current_id_sport = -1;
    private int failed_id_sport = -1;

    private int current_id_sportTypes = -1;

    public static final SportManager getInstance() {
        return instance;
    }

    private SportManager() {

    }

    // For Sport Objects

    protected int getNextIDSport(Connection con) throws SQLException {
        assertIsNonNullArgument(con, "Connection Object - getNextID");
        if (current_id_sport == -1) { // hoechste ID aus DB holen und auf current_id setzen, falls keine Eintraege
            // vorhanden => currend id = 0
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(id) FROM sport");
            current_id_sport = 0;
            if (rs.next()) {
                current_id_sport = rs.getInt(1);
            }
        }
        return ++current_id_sport;
    }

    protected Sport getSportFromID(int id, int sport_type_id) throws SQLException {
        assertIsValidID(id);
        Sport sport_unsaved = unsavedSports.get(id);
        if (sport_unsaved != null) {
            return sport_unsaved;
        }
        Statement st = getStatement();
        String sqlInquiry = "SELECT * FROM sportTypes WHERE id = ";
        sqlInquiry = sqlInquiry + String.valueOf(sport_type_id);
        ResultSet rs = st.executeQuery(sqlInquiry);
        rs.absolute(1);
        String type_name = rs.getString("Name");
        st = getStatement();
        sqlInquiry = "SELECT * FROM " + type_name + "_sportType WHERE id = ";
        sqlInquiry = sqlInquiry + String.valueOf(id);
        rs = st.executeQuery(sqlInquiry);
        rs.absolute(1);
        try {
            Sport sport = new Sport(rs);
            return sport;
        } catch (SQLException sex) {
            throw new IllegalArgumentException("No sport with ID: " + id + " found");
        }
    }

    // Insert new Row in Sport
    protected int insertData(Sport sport) throws SQLException { // TODO: weitere Attribute einfuegen
        assertIsNonNullArgument(sport, "Sport Object - insertData");
        DatabaseConnection dbcon = getDatabaseConnection();
        Connection con = dbcon.getRdbmsConnection();
        int id = getNextIDSport(con);
        Statement st = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = st.executeQuery("SELECT * FROM " + sport.getType().getName() + "_sportType");
        rs.moveToInsertRow();
        rs.updateInt("id", id);
        rs.updateString("name", sport.getName());
        rs.updateInt("sportType_id", sport.getSportType_id());
        for(int i = 0; i < sport.getAdditionalAttributes().length; i++){
            rs.updateString(i+3, sport.getAdditionalAttributes()[i]);
        }
        rs.insertRow();
        return id;
    }

    protected int tryInsertAgain(Sport sport) throws SQLException {
        assertIsNonNullArgument(sport, "Sport Object - tryInsertAgain");
        Statement st = getStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + sport.getType().getName() + "_sportType WHERE id = ");
        if (!rs.next()) {
            return insertData(sport); // nothing was inserted -> try insert again
        } else { // new row has already been inserted
            return current_id_sport;
        }
    }

    protected void update(Sport sport) {
        assertIsNonNullArgument(sport, "Sport Object - Update");
        int id = sport.getID();
        assertIsValidID(id);
        if (id < -1) {
            Sport sport_unsaved = unsavedSports.get(id);
            if (sport_unsaved == null) { // should never happen
                SysLog.logSysError("Sport not found");
                return;
            }
            return; // Sport is not in Database so no changes there needed
        }
        try {
            PreparedStatement stmt = getUpdatingStatement("SELECT * FROM " + sport.getType().getName() + "_sportType WHERE id = ");
            updateObject(sport, stmt);
        } catch (SQLException sex) {
            SysLog.logThrowable(sex);
        }
    }

    public Sport createSport(String typename, String sport_name) throws SQLException {
        SportType st = searchSportType(typename);
        if (st == null) {
            throw new IllegalArgumentException("Sporttype: " + typename + " doesn't exist");
        }
        Sport sp = st.createInstance(sport_name);
        sports.put(sp.getID(), sp);
        return sp;
    }

    protected int alternativeSave(Sport sport) {
        assertIsNonNullArgument(sport, "Sport Object - alternativeSave()");
        unsavedSports.put(--failed_id_sport, sport);
        return failed_id_sport;
    }

    public void saveAll() throws SQLException {
        for (Sport sport : unsavedSports.values()) {
            int id = sport.getID();
            for (int i = 0; i < 3; i++) { // 3 Tries
                try {
                    int oldid = sport.getID();
                    id = insertData(sport);
                    unsavedSports.remove(oldid);
                    break;
                } catch (SQLException sex) {
                    if (i == 2) {
                        SysLog.logSysError(
                                "Sport finally wasn't able to be inserted into the Database! id = " + sport.getID()); // TODO:
                                                                                                                      // Daten
                                                                                                                      // einfuegen
                        throw sex;
                    }
                }
            }
            sport.setDatabaseID(id);
        }
    }

    @Override
    protected Persistent createObject(ResultSet rset) throws SQLException { // TODO: checken ob Sport oder SportType
                                                                            // gemeint
        return new Sport(rset);
    }

    // For SportType Objects

    protected int getNextIDSportType(Connection con) throws SQLException {
        assertIsNonNullArgument(con, "Connection Object - getNextID");
        if (current_id_sportTypes == -1) { // hoechste ID aus DB holen und auf current_id setzen, falls keine Eintraege
            // vorhanden => currend id = 0
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(id) FROM sportTypes");
            current_id_sportTypes = 0;
            if (rs.next()) {
                current_id_sportTypes = rs.getInt(1);
            }
        }
        return ++current_id_sportTypes;
    }

    protected SportType getSportTypeFromID(int id) throws SQLException {
        if (id == 0) {
            return null;
        }
        Statement st = getStatement();
        String sqlInquiry = "SELECT * FROM sportTypes WHERE id = ";
        sqlInquiry = sqlInquiry + String.valueOf(id);
        ResultSet rs = st.executeQuery(sqlInquiry);
        rs.absolute(1);
        try {
            SportType sportType = new SportType(rs);
            return sportType;
        } catch (SQLException sex) {
            throw new IllegalArgumentException("No sportType with ID: " + id + " found");
        }
    }

    protected int insertData(SportType sportType) throws SQLException {
        assertIsNonNullArgument(sportType, "SportType Object - insertData");
        DatabaseConnection dbcon = getDatabaseConnection();
        Connection con = dbcon.getRdbmsConnection();
        int id = getNextIDSportType(con);
        Statement st = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = st.executeQuery("SELECT * FROM sportTypes");
        rs.moveToInsertRow();
        rs.updateInt("id", id);
        rs.updateString("name", sportType.getName());
        rs.updateString("attributes", sportType.getAttributesAsString());
        rs.updateString("subtypes", sportType.getSubtypesAsString());
        rs.updateInt("superType", sportType.getSuperType().getID());
        rs.insertRow();
        return id;
    }

    protected int tryInsertAgain(SportType sportType) throws SQLException {
        assertIsNonNullArgument(sportType, "SportType Object - tryInsertAgain");
        Statement st = getStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM sportTypes WHERE id = " + current_id_sportTypes);
        if (!rs.next()) {
            return insertData(sportType); // nothing was inserted -> try insert again
        } else { // new row has already been inserted
            return current_id_sportTypes;
        }
    }

    protected Statement getStatement() throws SQLException {
        DatabaseConnection dbcon = getDatabaseConnection();
        Connection con = dbcon.getRdbmsConnection();
        Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return st;
    }

    public SportType createSportType(String typename, SportType superType, String[] attributes) {
        assertIsNonNullArgument(typename, "Typename - createSportType");
        assertNoDuplicate(typename);
        SportType newS = new SportType(superType, typename, attributes);
        if (superType == null) {
            rootTypes.add(newS);
        }
        return newS;
    }

    protected SportType searchSportType(String typename) { // auch in Datenbank schauen
        if (rootTypes.size() > 0) {
            for (SportType type : rootTypes) {
                SportType result = type.hasSportType(typename);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    protected void removeRoot(SportType root) {
        rootTypes.remove(root);
    }

    protected void createNewSportTypeTable(SportType sportType) throws SQLException {
        Statement stmt = getStatement();
        String sqlQuery = "CREATE TABLE " + sportType.getName() + "_sportType " + "(id INTEGER not NULL, "
                + "name TEXT not NULL, " + "sportType_id INTEGER, ";
        String[] attr = sportType.getAttributes();
        for (int i = 0; i < attr.length; i++) {
            sqlQuery += attr[i] + " TEXT, ";
        }
        sqlQuery += "CONSTRAINT uniName UNIQUE(name), " + "PRIMARY KEY (id))";
        stmt.executeUpdate(sqlQuery);
    }

    protected void assertIsValidID(int id) {
        if (id == -1 || (id > current_id_sport && current_id_sport != -1)) {
            throw new IllegalArgumentException(id + " is not a valid ID");
        }
    }

    protected void assertNoDuplicate(String typename) {
        SportType result = searchSportType(typename);
        if (result != null) {
            if (result.getSuperType() == null) {
                throw new IllegalArgumentException("type already existing in: root");
            } else {
                throw new IllegalArgumentException("type already existing in: " + result.getSuperType().getName());
            }
        }

    }
}
