package org.wahlzeit.model;

public class CartesianCoordinate extends AbstractCoordinate {
    private double x;
    private double y;
    private double z;

    public CartesianCoordinate(double cx, double cy, double cz) {
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

    @Override
    public CartesianCoordinate asCartesianCoordinate() {
        return this;
    }

    @Override
    public SphericCoordinate asSphericCoordinate() {

        double radius = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        double phi = Math.acos(z / radius);
        double theta = Math.atan(y / x);
        return new SphericCoordinate(phi, theta, radius);
    }

    protected boolean doIsEqual(CartesianCoordinate c) {
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
