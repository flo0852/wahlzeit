package org.wahlzeit.model;

import java.sql.*;

@PatternInstance(
	patternName = "Abstract Factory",
	participants = {
		"ConcreteProduct2"
	}
)
@PatternInstance(
	patternName = "Decorator", 
	participants = { 
		"ConcreteComponent2" 
	}
)
public class SportPhoto extends Photo {

    private Sport sport;
    private SportType sportType;

    public SportPhoto() {
        super();
    }

    public SportPhoto(Sport sport){
        this.sport = sport;
        this.sportType = sport.getType();
    }

    public SportPhoto(PhotoId myId) {
        super();
    }
    public SportPhoto(ResultSet rs) throws SQLException {
        readFrom(rs);
    }

    public void readFrom(ResultSet rset) throws SQLException {
        super.readFrom(rset);
        sport = SportManager.getInstance().getSportFromID(rset.getInt("sport"), rset.getInt("sporttype"));
        sportType = sport.getType();
    }

    public void writeOn(ResultSet rset) throws SQLException {
        super.writeOn(rset);
        if (sport != null) {
            rset.updateInt("sport", sport.getID());
            rset.updateInt("sporttype", sportType.getID());
        }
        
    }

    /**
     * 
     * @methodtype get
     */
    public Sport getSport() {
        return sport;
    }

    /**
     * 
     * @methodtype set
     */
    public void setSport(Sport sport) {
        this.sport = sport;
        incWriteCount();
    }

}
