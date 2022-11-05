package org.wahlzeit.model;

import java.sql.*;

import org.wahlzeit.services.*;
import org.wahlzeit.utils.*;

public class SportPhoto extends Photo {

    protected String sport;

    public SportPhoto() {
        id = PhotoId.getNextId();
        incWriteCount();
    }

    public SportPhoto(PhotoId myId) {
        id = myId;
        incWriteCount();
    }

    public SportPhoto(ResultSet rs) throws SQLException {
        readFrom(rs);
    }

    public void readFrom(ResultSet rset) throws SQLException {
        id = PhotoId.getIdFromInt(rset.getInt("id"));

        ownerId = rset.getInt("owner_id");
        ownerName = rset.getString("owner_name");

        ownerNotifyAboutPraise = rset.getBoolean("owner_notify_about_praise");
        ownerEmailAddress = EmailAddress.getFromString(rset.getString("owner_email_address"));
        ownerLanguage = Language.getFromInt(rset.getInt("owner_language"));
        ownerHomePage = StringUtil.asUrl(rset.getString("owner_home_page"));

        width = rset.getInt("width");
        height = rset.getInt("height");

        tags = new Tags(rset.getString("tags"));

        status = PhotoStatus.getFromInt(rset.getInt("status"));
        praiseSum = rset.getInt("praise_sum");
        noVotes = rset.getInt("no_votes");

        creationTime = rset.getLong("creation_time");

        maxPhotoSize = PhotoSize.getFromWidthHeight(width, height);

        location_id = rset.getInt("location_id");

        sport = rset.getString("sport");
    }

    public void writeOn(ResultSet rset) throws SQLException {
        rset.updateInt("id", id.asInt());
        rset.updateInt("owner_id", ownerId);
        rset.updateString("owner_name", ownerName);
        rset.updateBoolean("owner_notify_about_praise", ownerNotifyAboutPraise);
        rset.updateString("owner_email_address", ownerEmailAddress.asString());
        rset.updateInt("owner_language", ownerLanguage.asInt());
        rset.updateString("owner_home_page", ownerHomePage.toString());
        rset.updateInt("width", width);
        rset.updateInt("height", height);
        rset.updateString("tags", tags.asString());
        rset.updateInt("status", status.asInt());
        rset.updateInt("praise_sum", praiseSum);
        rset.updateInt("no_votes", noVotes);
        rset.updateLong("creation_time", creationTime);
        if (location != null) {
            rset.updateInt("location_id", location.getID());
        }

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
