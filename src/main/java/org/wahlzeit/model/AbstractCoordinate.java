package org.wahlzeit.model;

import java.util.Objects;

import java.lang.Math;

public abstract class AbstractCoordinate implements Coordinate {
    private static final double radius_tolerance = 0.005;

    public double getCartesianDistance(Coordinate c) {
        // Preconditions
        assertIsNonNullArgument(c);

        // Method Code
        CartesianCoordinate c1 = c.asCartesianCoordinate();
        CartesianCoordinate c2 = asCartesianCoordinate();
        double[] c1val = new double[3];
        c1val[0] = c1.getXCoordinate();
        c1val[1] = c1.getYCoordinate();
        c1val[2] = c1.getZCoordinate();

        double dist = Math
                .sqrt(Math.pow(c1val[0] - c2.getXCoordinate(), 2.0) + Math.pow(c1val[1] - c2.getYCoordinate(), 2.0)
                        + Math.pow(c1val[2] - c2.getZCoordinate(), 2.0));
        return dist;
    }

    public double getCentralAngle(Coordinate c) {
        // Preconditions
        assertIsNonNullArgument(c);
        assertSimilarRadius(this, c);

        // Method Code
        SphericCoordinate c1 = this.asSphericCoordinate();
        SphericCoordinate c2 = c.asSphericCoordinate();

        double latitude1 = c1.getPhi() + 0.5 * Math.PI;
        double longitude1 = c1.getTheta();

        double latitude2 = c2.getPhi() + 0.5 * Math.PI;
        ;
        double longitude2 = c2.getTheta();

        double centralAngle = Math.acos(Math.sin(latitude1) * Math.sin(latitude2)
                + Math.cos(latitude1) * Math.cos(latitude2) * Math.cos(Math.abs(longitude2 - longitude1)));

        return centralAngle;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() == Coordinate.class || o.getClass() == CartesianCoordinate.class
                || o.getClass() == SphericCoordinate.class) {
            return isEqual((Coordinate) o);
        }
        return false;
    }

    @Override
    public boolean isEqual(Coordinate c) {
        // Preconditions
        assertIsNonNullArgument(c);

        // Method Code
        return asCartesianCoordinate().doIsEqual(c.asCartesianCoordinate());
    }

    @Override
    public int hashCode() {
        double[] rounded = roundCoordinates();
        return Objects.hash(rounded[0], rounded[1], rounded[2]);
    }

    private double[] roundCoordinates() {
        double[] new_cord = new double[3];
        double x_new = asCartesianCoordinate().getXCoordinate();
        double y_new = asCartesianCoordinate().getYCoordinate();
        double z_new = asCartesianCoordinate().getZCoordinate();
        double d = Math.pow(10, 6);
        new_cord[0] = Math.round(x_new * d) / d;
        new_cord[1] = Math.round(y_new * d) / d;
        new_cord[2] = Math.round(z_new * d) / d;
        return new_cord;
    }

    protected static void assertIsNonNullArgument(Object argument) {
        if (argument == null) {
            throw new IllegalArgumentException("Given Argument is null");
        }
    }

    protected static void assertSimilarRadius(Coordinate c1, Coordinate c2) {
        if (c1.asSphericCoordinate().getRadius() > c2.asSphericCoordinate().getRadius() * (1 + radius_tolerance) || c1
                .asSphericCoordinate().getRadius() < c2.asSphericCoordinate().getRadius() * (1 - radius_tolerance)) {
            throw new IllegalArgumentException("radius has to be the same at both Ccoordinates");
        }
    }

}
