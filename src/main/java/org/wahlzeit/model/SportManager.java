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

public class SportManager extends ObjectManager{
    private static final SportManager instance = new SportManager();

    private Set<SportType> rootTypes = new HashSet<SportType>();
    private Map<Integer, Sport> sports = new HashMap<Integer, Sport>();

    protected Map<Integer, Sport> unsavedSports = new HashMap<Integer, Sport>();

    private int current_id = -1;
    private int failed_id = -1;

    public static final SportManager getInstance() {
        return instance;
    }

    private SportManager() {

    }

    protected int getNextID(Connection con) throws SQLException {
        assertIsNonNullArgument(con, "Connection Object - getNextID");
        if (current_id == -1) { // hoechste ID aus DB holen und auf current_id setzen, falls keine Eintraege
                                // vorhanden => currend id = 0
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(id) FROM sport");
            current_id = 0;
            if (rs.next()) {
                current_id = rs.getInt(1);
            }
        }
        return ++current_id;
    }

    protected Sport getSportFromID(int id) throws SQLException {
        assertIsValidID(id);
        Sport sport_unsaved = unsavedSports.get(id);
        if (sport_unsaved != null) {
            return sport_unsaved;
        }
        Statement st = getStatement();
        String sqlInquiry = "SELECT * FROM sport WHERE id = ";
        sqlInquiry = sqlInquiry + String.valueOf(id);
        ResultSet rs = st.executeQuery(sqlInquiry);
        rs.absolute(1);
        try {
            Sport sport = new Sport(rs);
            return sport;
        } catch (SQLException sex) {
            throw new IllegalArgumentException("No sport with ID: " + id + " found");
        }
    }

    // Insert new Row in Sport
    protected int insertData(Sport sport) throws SQLException { //TODO: weitere Attribute einfuegen
        assertIsNonNullArgument(sport, "Sport Object - insertData");
        DatabaseConnection dbcon = getDatabaseConnection();
        Connection con = dbcon.getRdbmsConnection();
        int id = getNextID(con);
        Statement st = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = st.executeQuery("SELECT * FROM sport");
        rs.moveToInsertRow();
        rs.updateInt("id", id);
        rs.updateString("name", sport.getName());
        rs.insertRow();
        return id;
    }

    protected int tryInsertAgain(Sport sport) throws SQLException { //TODO: auf Sport ändern
        assertIsNonNullArgument(sport, "Sport Object - tryInsertAgain");
        Statement st = getStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM sport WHERE id = " + current_id);
        if (!rs.next()) {
            return insertData(sport); // nothing was inserted -> try insert again
        } else { // new row has already been inserted
            return current_id;
        }
    }

    protected Statement getStatement() throws SQLException {
        DatabaseConnection dbcon = getDatabaseConnection();
        Connection con = dbcon.getRdbmsConnection();
        Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return st;
    }


    public Sport createSport(String typename, String sport_name) throws SQLException{
        SportType st = getSportType(typename);
        Sport sp = st.createInstance(sport_name);
        sports.put(sp.getID(), sp);
        return sp;
    }

    public Sport createSport(String typename, String sport_name, String[] additionalAttributes, String[] additionalAttributesValues) {
        SportType st = getSportType(typename);
        Sport sp = st.createInstance(sport_name, additionalAttributes, additionalAttributesValues);
        sports.put(sp.getID(), sp);
        return sp;
    }

    public SportType getSportType(String typename) {
        SportType search = searchSportType(typename);
        if (search == null) {
            SportType res = new SportType(null, typename);
            rootTypes.add(res);
            return res;
        }
        return search;
    }

    protected SportType searchSportType(String typename) {
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

    protected void assertNoDuplicate(String typename) {
        SportType result = searchSportType(typename);
        if (result != null) {
            throw new IllegalArgumentException("type already existing in: " + result.getSuperType().name);
        }

    }

    protected void removeRoot(SportType root){
        rootTypes.remove(root);
    }

    protected void assertIsValidID(int id) {
        if (id == -1 || (id > current_id && current_id != -1)) {
            throw new IllegalArgumentException(id + " is not a valid ID");
        }
    }

    protected int alternativeSave(Sport sport) {
        assertIsNonNullArgument(sport, "Sport Object - alternativeSave()");
        unsavedSports.put(--failed_id, sport);
        return failed_id;
    }

    public void saveAll() throws SQLException{
        for (Sport sport : unsavedSports.values()) {
            int id = sport.getID();
            for (int i = 0; i < 3; i++) { // 3 Tries
                try {
                    int oldid = sport.getID();
                    id = insertData(sport);
                    unsavedSports.remove(oldid);
                    break;
                } catch (SQLException sex) {
                    if(i == 2){
                        SysLog.logSysError("Sport finally wasn't able to be inserted into the Database! id = " + sport.getID()); //TODO: Daten einfuegen
                        throw sex;
                    }
                }
            }
            sport.setDatabaseID(id);
        }
    }

    @Override
    protected Persistent createObject(ResultSet rset) throws SQLException {
        return new Sport(rset);
    }

}
