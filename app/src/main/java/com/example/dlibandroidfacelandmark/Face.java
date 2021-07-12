package com.example.dlibandroidfacelandmark;

import org.opencv.core.Rect;

import java.util.ArrayList;

public class Face {
    private Rect rect;
    private ArrayList<Position> positions;
    private int[] boundingBox;

    Face(Rect rect) {
        setBoundingBox(rect);
        this.positions = new ArrayList<Position>();
    }

    private void setBoundingBox(Rect r) {
        this.rect = r;
        this.boundingBox    = new int[4];
        this.boundingBox[0] = this.rect.x;
        this.boundingBox[1] = this.rect.y;
        this.boundingBox[2] = this.rect.width;
        this.boundingBox[3] = this.rect.height;
    }

    public int[] getBoundingBox() {
        return this.boundingBox;
    }

    public void setPositions(ArrayList<Position> positions) {
        this.positions = new ArrayList<Position>(positions);
    }

    public ArrayList<Position> getPositions() {
        return this.positions;
    }

    public void addPosition(int x, int y) {
        Position p = new Position(x, y);
        this.positions.add(p);
    }
}
