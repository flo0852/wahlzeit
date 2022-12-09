package org.wahlzeit.model;

import java.util.Objects;

import java.lang.Math;


import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCoordinate implements Coordinate {
    private static final double radius_tolerance = 0.005;
    protected static final Map<Integer, CartesianCoordinate> coordinateMap = new HashMap<Integer, CartesianCoordinate>();
    /*
     * Precondition: Argument not null
     */
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

    /*
     * Precondition 1: Argument not null
     * Precondition 2: Similar radius
     */
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
            return this.hashCode() == ((Coordinate) o).hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(asCartesianCoordinate().getXCoordinate(),asCartesianCoordinate().getYCoordinate(),asCartesianCoordinate().getZCoordinate());
    }

    protected static void assertIsNonNullArgument(Object argument) {
        if (argument == null) {
            throw new IllegalArgumentException("Given Argument is null");
        }
    }

    protected static void assertSimilarRadius(Coordinate c1, Coordinate c2) {
        if (c1.asSphericCoordinate().getRadius() > c2.asSphericCoordinate().getRadius() * (1 + radius_tolerance) || c1
                .asSphericCoordinate().getRadius() < c2.asSphericCoordinate().getRadius() * (1 - radius_tolerance)) {
            throw new IllegalArgumentException("radius has to be similar at both Coordinates");
        }
    }

    public static int getNumberCoordinateElements(){
        return coordinateMap.size();
    }

}
