package org.wahlzeit.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;

import org.wahlzeit.services.DataObject;
import org.wahlzeit.services.DatabaseConnection;
import org.wahlzeit.services.SessionManager;
import org.wahlzeit.services.ObjectManager;
import org.wahlzeit.services.Persistent;

public class LocationManager extends ObjectManager{

    protected static final LocationManager instance = new LocationManager();

    private int current_id = -1;
    public static final LocationManager getInstance(){
        return instance;
    }

    public LocationManager(){

    }

    protected int getNextID(Connection con) throws SQLException{
        if(current_id == -1){   //hoechste ID aus DB holen und auf current_id setzen, falls keine Eintraege vorhanden => currend id = 0
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(location_id) FROM location");
            current_id = 0;
            if(rs.next()){
                current_id = rs.getInt(1);
            }
        }
        return ++current_id;
    }


    protected int insertData(Coordinate c) throws SQLException{
        if (c == null) {
            throw new IllegalArgumentException("Given Coordinate is null");
        }
        DatabaseConnection dbcon = getDatabaseConnection();
        Connection con = dbcon.getRdbmsConnection();
        int id = getNextID(con);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM location");
        rs.moveToInsertRow();
        rs.updateInt("location_id", id);
        rs.updateDouble("x_coordinate", c.getCoordinates()[0]);
        rs.updateDouble("y_coordinate", c.getCoordinates()[1]);
        rs.updateDouble("z_coordinate", c.getCoordinates()[2]);
        rs.insertRow();
        return id;
    }

    protected Statement getStatement() throws SQLException{
        DatabaseConnection dbcon = getDatabaseConnection();
        Connection con = dbcon.getRdbmsConnection();
        Statement st = con.createStatement();
        return st;
    }

    @Override
    protected Persistent createObject(ResultSet rset) throws SQLException {
        return new Location(rset);
    }

}