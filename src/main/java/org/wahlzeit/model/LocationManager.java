package org.wahlzeit.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;

import org.wahlzeit.services.DatabaseConnection;
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

    protected Location getLocationFromID(int id) throws SQLException{
        Statement st = getStatement();
        String sqlInquiry = "SELECT * FROM location WHERE location_id = ";
        sqlInquiry = sqlInquiry + String.valueOf(id);
        ResultSet rs = st.executeQuery(sqlInquiry);
        rs.absolute(1); 
        return new Location(rs);
    }

    //Insert new Row in Location
    protected int insertData(Coordinate c) throws SQLException{
        if (c == null) {
            throw new IllegalArgumentException("Given Coordinate is null");
        }
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

    protected Statement getStatement() throws SQLException{
        DatabaseConnection dbcon = getDatabaseConnection();
        Connection con = dbcon.getRdbmsConnection();
        Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return st;
    }

    @Override
    protected Persistent createObject(ResultSet rset) throws SQLException {
        return new Location(rset);
    }

}