package org.wahlzeit.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;

import org.wahlzeit.services.DataObject;
import org.wahlzeit.services.DatabaseConnection;
import org.wahlzeit.services.SessionManager;

public class Location extends DataObject{

    private Coordinate cord;
    private int id;

    public Location(Coordinate c) throws SQLException{
        if (c == null) {
            throw new IllegalArgumentException("Given Coordinate is null");
        }
        cord = c;
        id = LocationManager.getInstance().insertData(cord);
    }
    
    //alternative Constructor
    public Location(double cx, double cy, double cz) throws SQLException{
        cord = new Coordinate(cx, cy, cz);
        id = LocationManager.getInstance().insertData(cord);
    }

    //Constructor for ResultSet
    public Location(ResultSet rset) throws SQLException{
        if (rset == null) {
            throw new IllegalArgumentException("Given Coordinate is null");
        }
        readFrom(rset);
    }

    //Getter for cord
    public Coordinate getCoordinate(){
        return cord;
    }

    //Setter for cord
    public void setCoordinate(Coordinate c) throws SQLException{
        if (c == null) {
            throw new IllegalArgumentException("Given Coordinate is null");
        }
        Statement st = LocationManager.getInstance().getStatement();
        String sqlInquiry = "SELECT * FROM location WHERE location_id = ";
        sqlInquiry = sqlInquiry + getIdAsString();
        ResultSet rs = st.executeQuery(sqlInquiry);
        rs.absolute(1); 
        cord = c;
        writeOn(rs);
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
        cord.setCoordinates(cx, cy, cz);
        
    }

    @Override
    public void writeOn(ResultSet rset) throws SQLException { //update Row
        rset.updateInt("location_id", id);
        rset.updateDouble("x_coordinate", cord.getCoordinates()[0]);
        rset.updateDouble("y_coordinate", cord.getCoordinates()[1]);
        rset.updateDouble("z_coordinate", cord.getCoordinates()[2]);
        rset.updateRow();
        
    }

    @Override
    public void writeId(PreparedStatement stmt, int pos) throws SQLException {
        stmt.setInt(pos,id);        
    }

    
    
}
