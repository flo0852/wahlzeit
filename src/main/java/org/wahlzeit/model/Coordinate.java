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

    public double getXCoordinate() {
        return x;
    }

    public double getYCoordinate() {
        return y;
    }

    public double getZCoordinate() {
        return z;
    }

    public void setCoordinates(double cx, double cy, double cz) {
        x = cx;
        y = cy;
        z = cz;
    }

    public double getDistance(Coordinate c) {
        if (c == null) {
            throw new IllegalArgumentException("Given Coordinate is null");
        }
        double[] c1val = new double[3];
        c1val[0] = c.getXCoordinate();
        c1val[1] = c.getYCoordinate();
        c1val[2] = c.getZCoordinate();

        double dist = Math
                .sqrt(Math.pow(c1val[0] - x, 2.0) + Math.pow(c1val[1] - y, 2.0) + Math.pow(c1val[2] - z, 2.0));
        return dist;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() == getClass()) {
            return isEqual((Coordinate) o);
        } else {
            return false;
        }
    }

    public boolean isEqual(Coordinate c) {
        double tolerance = 0.000001;
        if (c == null) {
            return false;
        }
        if (c.getXCoordinate() > (x + tolerance) || c.getXCoordinate() < (x - tolerance)) {
            return false;
        }

        if (c.getYCoordinate() > (y + tolerance) || c.getYCoordinate() < (y - tolerance)) {
            return false;
        }

        if (c.getZCoordinate() > (z + tolerance) || c.getZCoordinate() < (z - tolerance)) {
            return false;
        }
        return true;
    }
}
