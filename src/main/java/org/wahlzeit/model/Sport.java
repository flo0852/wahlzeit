package org.wahlzeit.model;

import java.util.concurrent.atomic.AtomicInteger;

public class Sport {
    private static AtomicInteger id_counter;
    public final SportType sport_type;
    public final String name;
    private int game_duration; //in minutes
    private String[][] additionalAttributes; //first dimension: Attribute Name, second Dimension: Attribute Value

    public Sport(SportType st, String name) {
        this.sport_type = st;
        this.name = name;

    }
    public Sport(SportType st, String name, int game_duration) {
        this.sport_type = st;
        this.name = name;
        this.game_duration = game_duration;
    }

    public Sport(SportType st, String name, int game_duration, String[][] additionalAttributes) {
        this.sport_type = st;
        this.name = name;
        this.game_duration = game_duration;
        this.additionalAttributes = additionalAttributes;
    }

    public String getName(){
        return name;
    }

    public int getDuration(){
        return game_duration;
    }

    public String getSpecificAdditionalAttribute(String attribute_name){
        return null;
    }

    public String getSpecificAdditionalAttribute(int index){
        if(additionalAttributes.length >= index){
            throw new IllegalArgumentException("Not a valid index");
        }
        return null;
    }

    public String[][] getAdditionalAttributes(){
        return additionalAttributes;
    }

    public int getID() {
        return id_counter.incrementAndGet();
    }

    public SportType getType() {
        return sport_type;
    }
}