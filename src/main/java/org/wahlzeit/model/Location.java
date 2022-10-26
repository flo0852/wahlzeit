package org.wahlzeit.model;

public class Location {

    private Coordinate cord;

    public Location(Coordinate c){
        if (c == null) {
            throw new IllegalArgumentException("Given Coordinate is null");
        }
        cord = c;
    }
    
    //alternative Constructor
    public Location(double cx, double cy, double cz){
        cord = new Coordinate(cx, cy, cz);
    }

    //Getter for cord
    public Coordinate getCoordinate(){
        return cord;
    }

    //Setter for cord
    public void setCoordinate(Coordinate c){
        if (c == null) {
            throw new IllegalArgumentException("Given Coordinate is null");
        }
        cord = c;
    }

    
    
}
