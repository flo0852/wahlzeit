package org.wahlzeit.model;

public class Coordinate {
    
    //Global Variables
    private double x;
    private double y;
    private double z;

    //Constructor
    public Coordinate(double cx, double cy, double cz){
        x = cx;
        y = cy;
        z = cz;
    }

    public double[] getCoordinates(){
        double[] ret = new double[3];
        ret[0] = x;
        ret[1] = y;
        ret[2] = z;
        return ret;
    }
}
