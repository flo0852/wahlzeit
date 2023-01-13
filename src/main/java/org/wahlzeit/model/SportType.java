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
    private int superType_id;
    private String name;
    private String[] attributes;
    private Set<Integer> subTypes = new HashSet<Integer>();

    public SportType(ResultSet rSet) throws SQLException {
        assertIsNonNullArgument(rSet, "ResultSet Object - SportType Constructor");
        for (int i = 0; i < 3; i++) {
            try {
                readFrom(rSet);
                return;
            } catch (SQLException sex) {
                sex.printStackTrace();
                if (i == 2) {
                    throw sex;
                }
                SysLog.logSysInfo("readFrom at SportType Constructor failed, trying again");
            }
        }
    }

    public SportType(SportType superType, String name, String[] attributes) throws SQLException {
        SportManager.getInstance().assertOnlyAllowedChars(attributes);
        SportManager.getInstance().assertNoDuplicate(name);
        this.superType = superType;
        if (superType != null) {
            this.superType_id = superType.getID();
        }
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
            sql_e1.printStackTrace();
            SysLog.logSysInfo("CREATE TABLE at SportType Constructor failed");
        }
        if (superType != null) {
            superType.subTypes.add(this.id);
            superType.incWriteCount();
            SportManager.getInstance().update(superType);
        }
    }

    public Sport createInstance(String sport_name, String[] additionalAttributes) throws SQLException {
        return new Sport(this, sport_name, additionalAttributes);
    }

    public Sport createInstance(String sport_name) throws SQLException {
        return new Sport(this, sport_name, null);
    }

    public int getID() {
        return id;
    }

    public SportType getSuperType() throws SQLException {
        if (superType != null) {
            return superType;
        }
        return SportManager.getInstance().getSportTypeFromID(superType_id);
    }

    public int getSuperTypeID() {
        return superType_id;
    }

    public String getName() {
        return name;
    }

    public String getTableName() {
        return name + "_sporttypes";
    }

    public String[] getAttributes() {
        return attributes;
    }

    public String getAttributesAsString() {
        if (attributes == null) {
            return null;
        }
        String res = "";
        for (String attr : attributes) {
            res += attr + "_";
        }
        return res;
    }

    public String getSubtypesAsString() {
        if (subTypes.size() == 0) {
            return null;
        }
        String res = "";
        for (int subt_id : subTypes) {
            res += Integer.toString(subt_id, 10) + "_";
        }
        return res;
    }

    private String[] StringToArray(String text) {
        if (text == null) {
            return null;
        }
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
        if (array == null) {
            return null;
        }
        int[] res = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = Integer.parseInt(array[i]);
        }
        return res;
    }

    public Iterator<Integer> getSubTypeIterator() {
        return subTypes.iterator();
    }

    public void newSubType(String name, String[] subtype_attributes) throws SQLException {
        SportManager.getInstance().assertNoDuplicate(name);
        SportType newSubType = new SportType(this, name, subtype_attributes);
        subTypes.add(newSubType.id);
    }

    public boolean hasInstance(Sport sport) throws SQLException {
        assert (sport != null) : "asked about null object";
        if (sport.getType() == this) {
            return true;
        }
        for (int type_id : subTypes) {
            if (SportManager.getInstance().getSportTypeFromID(type_id).hasInstance(sport)) {
                return true;
            }
        }
        return false;
    }

    public SportType hasSportType(String typename) throws SQLException {
        if (this.name == typename) {
            return this;
        }
        for (int type_id : subTypes) {
            SportType result = SportManager.getInstance().getSportTypeFromID(type_id).hasSportType(typename);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public boolean isSubtype(SportType subType) throws SQLException {
        assertIsNonNullArgument("sub", "SportType - isSubtype()");
        return hasSportType(subType.name) != null;
    }

    public void changeSuperType(SportType newSuperType) throws SQLException {
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
        String[] subtypes_ids = StringToArray(rset.getString("subtypes"));
        int[] subtype_ids = stringArrayToIntArray(subtypes_ids);
        if (subtype_ids != null) {
            for (int i = 0; i < subtypes_ids.length; i++) {
                subTypes.add(subtype_ids[i]);
            }
        }
    }

    @Override
    public void writeOn(ResultSet rset) throws SQLException {
        rset.updateInt("id", id);
        rset.updateString("Name", name);
        rset.updateString("attributes", getAttributesAsString());
        rset.updateString("subtypes", getSubtypesAsString());
        if (superType != null) {
            rset.updateInt("superType", superType_id);
        }
    }

    @Override
    public void writeId(PreparedStatement stmt, int pos) throws SQLException {
        stmt.setInt(pos, id);
    }

    private void assertExistingSportType(SportType type) throws SQLException {
        assertIsNonNullArgument(type, "SportType - assertExistingSportType()");
        if (SportManager.getInstance().getSportTypeFromName(type.name) == null) {
            throw new IllegalArgumentException("SportType - assertExistingSportType() should exist");
        }
    }

    private void assertIsNonNullArgument(Object arg, String label) {
        if (arg == null) {
            throw new IllegalArgumentException(label + " should not be null");
        }
    }

}