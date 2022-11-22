package org.wahlzeit.model;

public class SphericCoordinate extends AbstractCoordinate {
    private double phi;
    private double theta;
    private double radius;

    // Used for class Invariant
    private double oldphi;
    private double oldtheta;
    private double oldradius;

    public SphericCoordinate(double phi, double theta, double radius) {
        this.phi = phi;
        this.theta = theta;
        this.radius = radius;
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
        assertnoChangesStart();
        double x = radius * Math.sin(phi) * Math.cos(theta);
        double y = radius * Math.sin(phi) * Math.sin(theta);
        double z = radius * Math.cos(phi);
        assertnoChangesCheck();
        return new CartesianCoordinate(x, y, z);
    }

    @Override
    public SphericCoordinate asSphericCoordinate() {
        return this;
    }

    private void assertnoChangesStart() {
        oldphi = phi;
        oldtheta = theta;
        oldradius = radius;
    }

    private void assertnoChangesCheck() {
        if (phi != oldphi || theta != oldtheta || radius != oldradius) {
            rescueChanges();
        }
    }

    private void rescueChanges() {
        phi = oldphi;
        theta = oldtheta;
        radius = oldradius;
    }
}
