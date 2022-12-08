package org.wahlzeit.model;

import java.util.Objects;

public class SphericCoordinate extends AbstractCoordinate {
    private final double phi;
    private final double theta;
    private final double radius;

    private final CartesianCoordinate car_cord;

    private SphericCoordinate(double phi, double theta, double radius, CartesianCoordinate car_cord) {
        this.phi = phi;
        this.theta = theta;
        this.radius = radius;
        this.car_cord = car_cord;
    }

    public static SphericCoordinate getSphericCoordinateObject(double phi, double theta, double radius) {
        double x = radius * Math.sin(phi) * Math.cos(theta);
        double y = radius * Math.sin(phi) * Math.sin(theta);
        double z = radius * Math.cos(phi);

        double d = Math.pow(10, 6);
        double rx = Math.round(x * d) / d;
        double ry = Math.round(y * d) / d;
        double rz = Math.round(z * d) / d;

        double rphi = Math.round(phi * d) / d;
        double rtheta = Math.round(theta * d) / d;
        double rradius = Math.round(radius * d) / d;

        CartesianCoordinate car_cord = coordinateMap.get(Objects.hash(rx, ry, rz));
        if (car_cord == null) {
            return CartesianCoordinate.getCartesianCoordinateObject(rx, ry, rz).asSphericCoordinate();
        } else {
            if (!car_cord.SphericCoordinateAlreadyExists()) {
                return new SphericCoordinate(rphi, rtheta, rradius, car_cord);
            } else {
                return car_cord.asSphericCoordinate();
            }
        }
    }

    public double getPhi() {
        return phi;
    }

    public double getTheta() {
        return theta;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public CartesianCoordinate asCartesianCoordinate() {
        return car_cord;
    }

    @Override
    public SphericCoordinate asSphericCoordinate() {
        return this;
    }
}
