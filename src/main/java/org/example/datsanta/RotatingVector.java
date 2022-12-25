package org.example.datsanta;

public class RotatingVector {
    double x;
    double y;
    double restoreAngle;

    public RotatingVector(double x, double y) {
        this.x = x;
        this.y = y;
        this.restoreAngle = 0;
    }

    public double angle() {
        return Math.atan2(y, x);
    }

    public double length() {
        return Math.sqrt((x * x) + (y * y));
    }

    public void rotateCoordinates(double tiltAngle) {
        this.restoreAngle += tiltAngle;
        double angle = angle();
        double length = length();
        angle -= tiltAngle;
        x = length * Math.cos(angle);
        y = length * Math.sin(angle);
    }

    public void restoreCoordinates() {
        double angle = angle();
        double length = length();
        angle += restoreAngle;
        x = length * Math.cos(angle);
        y = length * Math.sin(angle);
        restoreAngle = 0;
    }
}