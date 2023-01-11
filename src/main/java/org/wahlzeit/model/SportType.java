package org.wahlzeit.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.wahlzeit.services.DataObject;
import org.wahlzeit.services.SysLog;

public class SportType extends DataObject {

    private int id;
    private SportType superType;
    private String name;
    private String[] attributes;
    private Set<SportType> subTypes = new HashSet<SportType>();

    public SportType(ResultSet rSet) throws SQLException {
        assertIsNonNullArgument(rSet, "ResultSet Object - SportType Constructor");
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

    public SportType(SportType superType, String name, String[] attributes) {
        SportManager.getInstance().assertNoDuplicate(name);
        this.superType = superType;
        this.name = name;
        this.attributes = attributes;
        // in datenbank einfuegen
        try {
            id = SportManager.getInstance().insertData(this); // 1. Try
        } catch (SQLException sql_e1) {
            try {
                SysLog.logSysInfo("insertData at SportType Constructor failed, trying again");
                id = SportManager.getInstance().tryInsertAgain(this); // 2. Try
            } catch (SQLException sql_e2) {
                SysLog.logSysInfo("insertData at SportType Constructor finally failed");
            }
        }
        // neue Tabelle in Datenbank anlegen
        try {
            SportManager.getInstance().createNewSportTypeTable(this);
        } catch (SQLException sql_e1) {
            SysLog.logSysInfo("CREATE TABLE at SportType Constructor failed");
        }
    }

    public Sport createInstance(String sport_name) throws SQLException {
        return new Sport(this, sport_name);
    }

    public int getID() {
        return id;
    }

    public SportType getSuperType() {
        return superType;
    }

    public String[] getAttributes() {
        return attributes;
    }

    public String getName() {
        return name;
    }

    public String getAttributesAsString() {
        String res = "";
        for (String attr : attributes) {
            res += attr + "_";
        }
        return res;
    }

    public String getSubtypesAsString() {
        String res = "";
        for (SportType subt : subTypes) {
            res += subt.getIdAsString() + "_";
        }
        return res;
    }

    private String[] StringToArray(String text) {
        int word_counter = 0;
        char[] textArray = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            if (textArray[i] == '_') {
                word_counter++;
            }
        }
        int counter = 0;
        String[] res = new String[word_counter];
        for (int i = 0; i < res.length; i++) {
            res[i] = "";
        }
        for (int i = 0; i < text.length(); i++) {
            if (textArray[i] == '_') {
                counter++;
            } else {
                res[counter] += textArray[i];
            }
        }
        return res;
    }

    private int[] stringArrayToIntArray(String[] array) {
        int[] res = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = Integer.parseInt(array[i]);
        }
        return res;
    }

    public Iterator<SportType> getSubTypeIterator() {
        return subTypes.iterator();
    }

    public void newSubType(String name, String[] subtype_attributes) {
        SportManager.getInstance().assertNoDuplicate(name);
        SportType newSubType = new SportType(this, name, subtype_attributes);
        subTypes.add(newSubType);
    }

    public boolean hasInstance(Sport sport) {
        assert (sport != null) : "asked about null object";
        if (sport.getType() == this) {
            return true;
        }
        for (SportType type : subTypes) {
            if (type.hasInstance(sport)) {
                return true;
            }
        }
        return false;
    }

    public SportType hasSportType(String typename) {
        if (this.name == typename) {
            return this;
        }
        for (SportType type : subTypes) {
            SportType result = type.hasSportType(typename);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public boolean isSubtype(SportType subType) {
        assertIsNonNullArgument("sub", "SportType - isSubtype()");
        return hasSportType(subType.name) != null;
    }

    public void changeSuperType(SportType newSuperType) {
        assertExistingSportType(newSuperType);
        if (superType == null) {
            SportManager.getInstance().removeRoot(newSuperType);
        } else {
            superType.removeSubType(this);
        }
        superType = newSuperType;
    }

    private void removeSubType(SportType type) {
        subTypes.remove(type);
    }

    @Override
    public String getIdAsString() {
        return String.valueOf(id);
    }

    @Override
    public void readFrom(ResultSet rset) throws SQLException {
        id = rset.getInt("id");
        name = rset.getString("Name");
        superType = SportManager.getInstance().getSportTypeFromID(rset.getInt("superType"));
        attributes = StringToArray(rset.getString("attributes"));
        String[] subtypes_ids = StringToArray(rset.getString("attributes"));
        int[] subtype_ids = stringArrayToIntArray(subtypes_ids);
        for (int i = 0; i < subtypes_ids.length; i++) {
            subTypes.add(SportManager.getInstance().getSportTypeFromID(subtype_ids[i]));
        }
    }

    @Override
    public void writeOn(ResultSet rset) throws SQLException {
        rset.updateInt("id", id);
        rset.updateString("Name", name);
        rset.updateString("attributes", getAttributesAsString());
        rset.updateString("subtypes", getSubtypesAsString());
        rset.updateInt("superType", superType.getID());
    }

    @Override
    public void writeId(PreparedStatement stmt, int pos) throws SQLException {
        stmt.setInt(pos, id);
    }

    private void assertExistingSportType(SportType type) {
        assertIsNonNullArgument(type, "SportType - assertExistingSportType()");
        if (SportManager.getInstance().searchSportType(type.name) == null) {
            throw new IllegalArgumentException("SportType - assertExistingSportType() should exist");
        }
    }

    private void assertIsNonNullArgument(Object arg, String label) {
        if (arg == null) {
            throw new IllegalArgumentException(label + " should not be null");
        }
    }

}