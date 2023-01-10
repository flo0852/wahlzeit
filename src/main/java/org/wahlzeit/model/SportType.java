package org.wahlzeit.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SportType {

    public final SportType superType;
    public final String name;
    protected Set<SportType> subTypes = new HashSet<SportType>();

    public SportType(SportType superType, String name) {
        this.superType = superType;
        this.name = name;
    }

    public Sport createInstance(String sport_name) {
        return new Sport(this,name);
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
}