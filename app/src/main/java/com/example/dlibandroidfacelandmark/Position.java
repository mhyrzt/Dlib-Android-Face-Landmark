package com.example.dlibandroidfacelandmark;

public class Position {
    private double x;
    private double y;

    Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double[] getPosition() {
        double[] p = {this.x, this.y};
        return p;
    }
}
