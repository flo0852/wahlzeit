package org.wahlzeit.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.wahlzeit.services.DatabaseConnection;
import org.wahlzeit.services.ObjectManager;
import org.wahlzeit.services.Persistent;
import org.wahlzeit.services.SysLog;

public class LocationManager extends ObjectManager {

    protected static final LocationManager instance = new LocationManager();

    private int current_id = -1;

    public static final LocationManager getInstance() {
        return instance;
    }

    public LocationManager() {

    }

    protected int getNextID(Connection con) throws SQLException {
        assertIsNonNullArgument(con, "Connection Object - getNextID");
        if (current_id == -1) { // hoechste ID aus DB holen und auf current_id setzen, falls keine Eintraege
                                // vorhanden => currend id = 0
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(location_id) FROM location");
            current_id = 0;
            if (rs.next()) {
                current_id = rs.getInt(1);
            }
        }
        return ++current_id;
    }

    protected Location getLocationFromID(int id) throws SQLException {
        Statement st = getStatement();
        String sqlInquiry = "SELECT * FROM location WHERE location_id = ";
        sqlInquiry = sqlInquiry + String.valueOf(id);
        ResultSet rs = st.executeQuery(sqlInquiry);
        rs.absolute(1);
        try {
            Location loc = new Location(rs);
            return loc;
        } catch (SQLException sex) {
            throw new IllegalArgumentException("No location with ID: " + id + " found");
        }
    }

    // Insert new Row in Location
    protected int insertData(CartesianCoordinate c) throws SQLException {
        assertIsNonNullArgument(c, "CartesianCoordinate Object - insertData");
        DatabaseConnection dbcon = getDatabaseConnection();
        Connection con = dbcon.getRdbmsConnection();
        int id = getNextID(con);
        Statement st = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = st.executeQuery("SELECT * FROM location");
        rs.moveToInsertRow();
        rs.updateInt("location_id", id);
        rs.updateDouble("x_coordinate", c.getXCoordinate());
        rs.updateDouble("y_coordinate", c.getYCoordinate());
        rs.updateDouble("z_coordinate", c.getZCoordinate());
        rs.insertRow();
        return id;
    }

    protected int tryInsertAgain(Location loc, CartesianCoordinate c) throws SQLException {
        assertIsNonNullArgument(c, "CartesianCoordinate Object - tryInsertAgain");
        Statement st = getStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM location WHERE location_id = " + current_id);
        if (!rs.next()) {
            return insertData(c); //nothing was inserted -> try insert again
        } else { //new row with at least the id has been inserted -> insert the rest
            rs.updateDouble("x_coordinate", c.getXCoordinate());
            rs.updateDouble("y_coordinate", c.getYCoordinate());
            rs.updateDouble("z_coordinate", c.getZCoordinate());
            rs.updateRow();
        }
        return current_id;
    }

    protected void updateCoordinate(Location loc, CartesianCoordinate c, int id) {
        assertIsNonNullArgument(loc, "Location Object - updateCoordinate()");
        assertIsValidID(id);
        try {
            PreparedStatement stmt = getUpdatingStatement("SELECT * FROM location WHERE location_id = ?");
            updateObject(loc, stmt);
        } catch (SQLException sex) {
            SysLog.logThrowable(sex);
        }
    }

    protected Statement getStatement() throws SQLException {
        DatabaseConnection dbcon = getDatabaseConnection();
        Connection con = dbcon.getRdbmsConnection();
        Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return st;
    }

    @Override
    protected Persistent createObject(ResultSet rset) throws SQLException {
        return new Location(rset);
    }

    protected void assertIsValidID(int id) {
        if (id < 0 || id > current_id) {
            throw new IllegalArgumentException(id + " is not a valid ID");
        }
    }

}