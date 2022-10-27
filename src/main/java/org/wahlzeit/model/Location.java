package org.wahlzeit.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.services.DataObject;
import org.wahlzeit.services.DatabaseConnection;
import org.wahlzeit.services.SessionManager;

public class Location extends DataObject{

    private Coordinate cord;
    private static int current_id = -1;
    private int id;

    public Location(Coordinate c) throws SQLException{
        if(current_id == -1){   //hoechste ID aus DB holen und auf current_id setzen, falls keine Eintraege vorhanden => currend id = 0
            DatabaseConnection dbcon = SessionManager.getDatabaseConnection();
            java.sql.Connection con = dbcon.getRdbmsConnection();
            java.sql.Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(location_id) FROM location");
            current_id = 0;
            if(rs.next()){
                current_id = rs.getInt(1);
            }
        }

        if (c == null) {
            throw new IllegalArgumentException("Given Coordinate is null");
        }
        id = ++current_id;
        cord = c;
    }
    
    //alternative Constructor
    public Location(double cx, double cy, double cz) throws SQLException{
        if(current_id == -1){   //hoechste ID aus DB holen und auf current_id setzen, falls keine Eintraege vorhanden => currend id = 0
            DatabaseConnection dbcon = SessionManager.getDatabaseConnection();
            java.sql.Connection con = dbcon.getRdbmsConnection();
            java.sql.Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(location_id) FROM location");
            current_id = 0;
            if(rs.next()){
                current_id = rs.getInt(1);
            }
        }
        id = ++current_id;
        cord = new Coordinate(cx, cy, cz);
    }

    //Constructor for ResultSet
    public Location(ResultSet rset) throws SQLException{
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
        DatabaseConnection dbcon = SessionManager.getDatabaseConnection();
        java.sql.Connection con = dbcon.getRdbmsConnection();
        java.sql.Statement st = con.createStatement();
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
    public void writeOn(ResultSet rset) throws SQLException {
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
