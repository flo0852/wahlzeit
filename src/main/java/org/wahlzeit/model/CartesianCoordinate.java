package org.wahlzeit.model;

public class CartesianCoordinate extends AbstractCoordinate {
    private final double x;
    private final double y;
    private final double z;

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

    @Override
    public CartesianCoordinate asCartesianCoordinate() {
        return this;
    }

    @Override
    public SphericCoordinate asSphericCoordinate() {
        double xe = x;
        if (x == 0.0) {
            xe = 0.0000001;
        }
        double radius = 0.0;
        if (y == 0 && z == 0) {
            radius = Math.sqrt(Math.pow(xe, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        } else {
            radius = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        }
        double phi = Math.acos(z / radius);
        double theta = Math.atan(y / xe);
        return new SphericCoordinate(phi, theta, radius);
    }

    /*
     * Precondition: Argument not null
     */
    protected boolean doIsEqual(CartesianCoordinate c) {
        // Preconditions
        assertIsNonNullArgument(c);

        // Method Code
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
