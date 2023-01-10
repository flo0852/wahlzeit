package org.wahlzeit.model;

import java.util.HashMap;
import java.util.Map;

import java.util.HashSet;
import java.util.Set;

public class SportManager {
    private static final SportManager instance = new SportManager();

    private Set<SportType> rootTypes = new HashSet<SportType>();
    private Map<Integer, Sport> sports = new HashMap<Integer, Sport>();

    public static final SportManager getInstance() {
        return instance;
    }

    private SportManager() {

    }

    public Sport createSport(String typename, String sport_name) {
        SportType st = getSportType(typename);
        Sport sp = st.createInstance(sport_name);
        sports.put(sp.getID(), sp);
        return sp;
    }

    public Sport createSport(String typename, String sport_name, String[] additionalAttributes, String[] additionalAttributesValues) {
        SportType st = getSportType(typename);
        Sport sp = st.createInstance(sport_name, additionalAttributes, additionalAttributesValues);
        sports.put(sp.getID(), sp);
        return sp;
    }

    public SportType getSportType(String typename) {
        SportType search = searchSportType(typename);
        if (search == null) {
            SportType res = new SportType(null, typename);
            rootTypes.add(res);
            return res;
        }
        return search;
    }

    protected SportType searchSportType(String typename) {
        if (rootTypes.size() > 0) {
            for (SportType type : rootTypes) {
                SportType result = type.hasSportType(typename);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    protected void assertNoDuplicate(String typename) {
        SportType result = searchSportType(typename);
        if (result != null) {
            throw new IllegalArgumentException("type already existing in: " + result.getSuperType().name);
        }

    }

    protected void removeRoot(SportType root){
        rootTypes.remove(root);
    }

}
