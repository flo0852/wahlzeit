package org.wahlzeit.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.services.DataObject;
import org.wahlzeit.services.SysLog;

public class Location extends DataObject {

    private Coordinate cord;
    private int id = -1;

    public Location(Coordinate c) throws SQLException {
        assertIsNonNullArgument(c, "Coordinate Object - Location Constructor");
        cord = c;
        try {
            id = LocationManager.getInstance().insertData(cord.asCartesianCoordinate()); // 1. Try
        } catch (SQLException sql_e1) {
            try {
                SysLog.logSysInfo("insertData at Location Constructor failed, trying again");
                id = LocationManager.getInstance().tryInsertAgain(this, cord.asCartesianCoordinate()); // 2. Try
            } catch (SQLException sql_e2) {
                SysLog.logSysInfo("insertData at Location Constructor failed, trying again");
                id = LocationManager.getInstance().tryInsertAgain(this, cord.asCartesianCoordinate()); // 3. Try
            }
        }
    }

    // alternative Constructor
    public Location(double cx, double cy, double cz) throws SQLException {
        cord = new CartesianCoordinate(cx, cy, cz);
        try {
            id = LocationManager.getInstance().insertData(cord.asCartesianCoordinate()); // 1. Try
        } catch (SQLException sql_e1) {
            try {
                SysLog.logSysInfo("insertData at Location Constructor failed, trying again");
                id = LocationManager.getInstance().tryInsertAgain(this, cord.asCartesianCoordinate()); // 2. Try
            } catch (SQLException sql_e2) {
                SysLog.logSysInfo("insertData at Location Constructor failed, trying again");
                id = LocationManager.getInstance().tryInsertAgain(this, cord.asCartesianCoordinate()); // 3. Try
            }
        }
    }

    // Constructor for ResultSet
    public Location(ResultSet rset) throws SQLException {
        assertIsNonNullArgument(rset, "ResultSet Object - Location Constructor");
        for (int i = 0; i < 2; i++) {
            try {
                readFrom(rset);
                return;
            } catch (SQLException sex) {
                SysLog.logSysInfo("readFrom at Location Constructor failed, trying again");
            }
        }
        readFrom(rset);
    }

    public int getID() {
        return id;
    }

    // Getter for Coordinate

    public CartesianCoordinate getCartesianCoordinate() {
        return cord.asCartesianCoordinate();
    }

    public SphericCoordinate getSphericCoordinate() {
        return cord.asSphericCoordinate();
    }

    public Coordinate getCoordinate() {
        return cord;
    }

    // Setter for cord
    public void setCoordinate(Coordinate c) {
        assertIsNonNullArgument(c, "Coordinate Object - setCoordinate()");
        cord = c.asCartesianCoordinate();
        incWriteCount();
        LocationManager.getInstance().updateCoordinate(this, c.asCartesianCoordinate(), id);
    }

    @Override
    public String getIdAsString() {
        return String.valueOf(id);
    }

    @Override
    public void readFrom(ResultSet rset) throws SQLException {
        id = rset.getInt("location_id");
        double cx = rset.getDouble("x_coordinate");
        double cy = rset.getDouble("y_coordinate");
        double cz = rset.getDouble("Z_coordinate");
        cord = new CartesianCoordinate(cx, cy, cz);

    }

    @Override
    public void writeOn(ResultSet rset) throws SQLException { // update Row
        rset.updateInt("location_id", id);
        rset.updateDouble("x_coordinate", cord.asCartesianCoordinate().getXCoordinate());
        rset.updateDouble("y_coordinate", cord.asCartesianCoordinate().getYCoordinate());
        rset.updateDouble("z_coordinate", cord.asCartesianCoordinate().getZCoordinate());
        rset.updateRow();

    }

    @Override
    public void writeId(PreparedStatement stmt, int pos) throws SQLException {
        stmt.setInt(pos, id);
    }

    protected void assertIsNonNullArgument(Object arg, String label) {
        if (arg == null) {
            throw new IllegalArgumentException(label + " should not be null");
        }
    }

}
