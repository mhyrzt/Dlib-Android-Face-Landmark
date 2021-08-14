package com.example.dlibandroidfacelandmark;

public class Position {
    private double x;
    private double y;

    Position(double x, double y) {
        this.x = x;
        this.y = y;
    }
    Position(int x, int y) {
        this((double) x, (double) y);
    }
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
