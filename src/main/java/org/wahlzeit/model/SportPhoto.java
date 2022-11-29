package org.wahlzeit.model;

import java.sql.*;

public class SportPhoto extends Photo {

    protected String sport;

    public SportPhoto() {
        super();
    }

    public SportPhoto(PhotoId myId) {
        super();
    }
    public SportPhoto(ResultSet rs) throws SQLException {
        readFrom(rs);
    }

    public void readFrom(ResultSet rset) throws SQLException {
        super.readFrom(rset);
        sport = rset.getString("sport");
    }

    public void writeOn(ResultSet rset) throws SQLException {
        super.writeOn(rset);
        if (sport != null) {
            rset.updateString("sport", sport);
        }
    }

    /**
     * 
     * @methodtype get
     */
    public String getSport() {
        return sport;
    }

    /**
     * 
     * @methodtype set
     */
    public void setSport(String sport) {
        this.sport = sport;
        incWriteCount();
    }

}
