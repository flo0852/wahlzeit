package org.wahlzeit.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SportType {

    private SportType superType;
    public final String name;
    private Set<SportType> subTypes = new HashSet<SportType>();

    public SportType(SportType superType, String name) {
        this.superType = superType;
        this.name = name;
    }

    public Sport createInstance(String sport_name) {
        return new Sport(this,name);
    }

    public Sport createInstance(String sport_name, String[] additionalAttributes, String[] additionalAttributesValues) {
        return new Sport(this,name, additionalAttributes, additionalAttributesValues);
    }

    public SportType getSuperType() {
        return superType;
    }

    public Iterator<SportType> getSubTypeIterator() {
        return subTypes.iterator();
    }

    public void newSubType(String name) {
        SportManager.getInstance().assertNoDuplicate(name);
        SportType newSubType = new SportType(this, name);
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

    public SportType hasSportType(String typename){
        if(this.name == typename){
            return this;
        }
        for(SportType type : subTypes){
            SportType result = type.hasSportType(typename);
            if(result != null){
                return result;
            }
        }
        return null;
    }

    public boolean isSubtype(SportType subType){
        assertIsNonNullArgument("sub", "SportType - isSubtype()");
        return hasSportType(subType.name) != null;
    }


    public void changeSuperType(SportType newSuperType){
        assertExistingSportType(newSuperType);
        if(superType == null){
            SportManager.getInstance().removeRoot(newSuperType);
        }
        else{
            superType.removeSubType(this);
        }
        superType = newSuperType;
    }

    private void removeSubType(SportType type){
        subTypes.remove(type);
    }

    private void assertExistingSportType(SportType type){
        assertIsNonNullArgument(type, "SportType - assertExistingSportType()");
        if(SportManager.getInstance().searchSportType(type.name) == null){
            throw new IllegalArgumentException("SportType - assertExistingSportType() should exist");
        }
    }


    private void assertIsNonNullArgument(Object arg, String label) {
        if (arg == null) {
            throw new IllegalArgumentException(label + " should not be null");
        }
    }
}