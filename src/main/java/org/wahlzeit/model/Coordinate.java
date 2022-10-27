package org.wahlzeit.model;

public class Coordinate {

    // Global Variables
    private double x;
    private double y;
    private double z;

    // Constructor
    public Coordinate(double cx, double cy, double cz) {
        x = cx;
        y = cy;
        z = cz;
    }

    public double[] getCoordinates() { // nachreichen warum return Array => selten nur eine Koordinate benötigt =>
                                       // meist alle 3, wenn einzeln, dann mehr methodenafurufe
        double[] ret = new double[3];
        ret[0] = x;
        ret[1] = y;
        ret[2] = z;
        return ret;
    }

    public void setCoordinates(double cx, double cy, double cz){
        x = cx;
        y = cy;
        z = cz;
    }

    public double getDistance(Coordinate c) {
        if (c == null) {
            throw new IllegalArgumentException("Given Coordinate is null");
        }
        double[] c1val = c.getCoordinates();
        double dist = Math
                .sqrt(Math.pow(c1val[0] - x, 2.0) + Math.pow(c1val[1] - y, 2.0) + Math.pow(c1val[2] - z, 2.0));
        return dist;
    }

    @Override
    public boolean equals(Object o){
        if(o == null){
            throw new IllegalArgumentException("Given Coordinate is null");
        }
        if(o.getClass() == getClass()){
            return isEqual((Coordinate) o);
        }
        else{
            return false;
        }
    }
    public boolean isEqual(Coordinate c) {
        if (c == null) {
            throw new IllegalArgumentException("Given Coordinate is null");
        }
        if (c.x == x && c.y == y && c.z == z) {
            return true;
        }
        return false;
    }
}
