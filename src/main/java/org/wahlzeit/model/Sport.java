package org.wahlzeit.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.wahlzeit.services.DataObject;
import org.wahlzeit.services.SysLog;

public class Sport extends DataObject { // TODO: Getter und Setter
    private int id = -1; // >= 0 := valid ID
                         // -1 := no ID
                         // < -1 := provisionally ID -> Inserting in Database failed -> only saved in
                         // Cache private SportType sport_type;
    private String name;
    private SportType sport_type;
    private int sportType_id;
    private String[] additionalAttributes;

    public Sport(ResultSet rSet) throws SQLException {
        assertIsNonNullArgument(rSet, "ResultSet Object - Sport Constructor");
        for (int i = 0; i < 3; i++) {
            try {
                readFrom(rSet);
                return;
            } catch (SQLException sex) {
                if (i == 2) {
                    throw sex;
                }
                SysLog.logSysInfo("readFrom at Sport Constructor failed, trying again");
            }
        }
    }

    public Sport(SportType st, String name, String[] additionalAttributes) {
        assertIsNonNullArgument(st, "SportType Obect - Sport Constructor");
        assertIsNonNullArgument(name, "String Obect - Sport Constructor");
        this.sport_type = st;
        this.sportType_id = st.getID();
        this.name = name;
        this.additionalAttributes = additionalAttributes;

        try {
            id = SportManager.getInstance().insertData(this); // 1. Try
        } catch (SQLException sql_e1) {
            try {
                SysLog.logSysInfo("insertData at Sport Constructor failed, trying again");
                id = SportManager.getInstance().tryInsertAgain(this); // 2. Try
            } catch (SQLException sql_e2) {
                SysLog.logSysInfo("insertData at Sport Constructor finally failed, adding in Sport Cache");
                id = SportManager.getInstance().alternativeSave(this);
            }
        }
    }

    public int getID() {
        return id;
    }

    public SportType getType() {
        return sport_type;
    }

    public String getName() {
        return name;
    }

    public String getSpecificAdditionalAttribute(String attribute_name) {
        int index = getIndex(attribute_name);
        return additionalAttributes[index];
    }

    public String getSpecificAdditionalAttribute(int index) {
        if (additionalAttributes.length >= index) {
            throw new IllegalArgumentException("Not a valid index");
        }
        return additionalAttributes[index];
    }

    public String[] getAdditionalAttributes() {
        return this.additionalAttributes;
    }

    public int getSportType_id(){
        return sportType_id;
    }
    public void setSpecificAdditionalAttribute(String attribute_name, String value) {
        int index = getIndex(attribute_name);
        additionalAttributes[index] = value;
    }

    public void setSpecificAdditionalAttribute(int index, String value) {
        if (additionalAttributes.length >= index) {
            throw new IllegalArgumentException("Not a valid index");
        } else {
            additionalAttributes[index] = value;
        }
    }

    private int getIndex(String attribute_name) {
        String[] attr = sport_type.getAttributes();
        for (int i = 0; i < attr.length; i++) {
            if (attr[i] == attribute_name) {
                return i;
            }
        }
        throw new IllegalArgumentException(attribute_name + " is not a valid attribute name");
    }

    @Override
    public String getIdAsString() {
        return String.valueOf(id);
    }

    @Override
    public void readFrom(ResultSet rset) throws SQLException {
        id = rset.getInt("id");
        name = rset.getString("Name");
        sportType_id = rset.getInt("sportType_id");
        sport_type = SportManager.getInstance().getSportTypeFromID(sportType_id);
        additionalAttributes = new String[sport_type.getAttributes().length];
        for(int i = 0; i < additionalAttributes.length; i++){
            additionalAttributes[i] = rset.getString(i + 3);
        }
    }

    @Override
    public void writeOn(ResultSet rset) throws SQLException {
        rset.updateInt("id", id);
        rset.updateString("Name", name);
        rset.updateInt("sportType_id", sportType_id);
        for(int i = 0; i < additionalAttributes.length; i++){
            rset.updateString(i+3, additionalAttributes[i]);
        }

    }

    @Override
    public void writeId(PreparedStatement stmt, int pos) throws SQLException {
        stmt.setInt(pos, id);

    }

    protected void setDatabaseID(int positive_id) {
        if (id < -1) {
            id = positive_id;
        } else { // IDs from sports which are already in the database mustn't be changed
            throw new IllegalAccessError(
                    "Changes on IDs of sports which are already in the Database are strictly forbidden");
        }
    }

    protected void assertIsNonNullArgument(Object arg, String label) {
        if (arg == null) {
            throw new IllegalArgumentException(label + " should not be null");
        }
    }

    protected void assertIsSameLength(String[] arg1, String[] arg2) {
        if (arg1.length != arg2.length) {
            throw new IllegalArgumentException("Array lengths should be the same");
        }
    }

}