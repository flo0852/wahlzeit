package org.wahlzeit.model;

import java.util.Objects;

@PatternInstance(
	patternName = "Flyweight",
	participants = {
		"ConcreteFlyweight",
        "FlyweightFactory"
	}
)
public class CartesianCoordinate extends AbstractCoordinate {
    private final double x;
    private final double y;
    private final double z;
    private SphericCoordinate sph_cord;

    private CartesianCoordinate(double cx, double cy, double cz) {
        x = cx;
        y = cy;
        z = cz;
        coordinateMap.put(Objects.hash(x, y, z), this);

    }

    public static CartesianCoordinate getCartesianCoordinateObject(double x, double y, double z) {
        double d = Math.pow(10, 6);
        double rx = Math.round(x * d) / d;
        double ry = Math.round(y * d) / d;
        double rz = Math.round(z * d) / d;
        CartesianCoordinate cord = coordinateMap.get(Objects.hash(rx, ry, rz));
        if (cord == null) {
            return new CartesianCoordinate(rx, ry, rz);
        }
        return cord;
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
        if (sph_cord == null) {
            double radius = 0.0;
            radius = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
            if(radius == 0){
                return SphericCoordinate.getSphericCoordinateObject(0, 0, 0);
            }
            double phi = Math.acos(z / radius);
            double theta = 0.0;
            if (x == 0) {
                 theta = Math.signum(y) * Math.PI * 0.5;
            } else {
                 theta = Math.atan(y / x);
            }
            if (theta < 0) {
                theta = theta + Math.PI;
            }
            sph_cord = SphericCoordinate.getSphericCoordinateObject(phi, theta, radius);
        }
        return sph_cord;
    }

    protected boolean SphericCoordinateAlreadyExists() {
        if (sph_cord == null) {
            return false;
        }
        return true;
    }
}