package com.example.dlibandroidfacelandmark;

import java.util.ArrayList;

public class Face {
    private ArrayList<Position> landMarks;
    private long count;

    Face() {
        landMarks = new ArrayList<Position>();
        count = 0;
    }

    public void addPosition(Position pos) {
        landMarks.add(pos);
        count++;
    }

    public void addPosition(double x, double y) {
        Position pos = new Position(x, y);
        this.addPosition(pos);
    }

    public ArrayList<Position> getLandMarks() {
        return this.landMarks;
    }

    public long getCount() {
        return this.count;
    }
}
