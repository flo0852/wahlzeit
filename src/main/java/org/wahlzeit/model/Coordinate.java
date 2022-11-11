package org.wahlzeit.model;

public interface Coordinate {
    public CartesianCoordinate asCartesianCoordinate();

    public double getCartesianDistance(Coordinate c);

    public SphericCoordinate asSphericCoordinate();

    public double getCentralAngle(Coordinate c);

    public boolean isEqual(Coordinate c);
}
