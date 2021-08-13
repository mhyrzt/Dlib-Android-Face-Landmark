package com.example.dlibandroidfacelandmark;

import org.opencv.core.Rect;

import java.util.ArrayList;

public class Face {
    private Rect rect;
    private ArrayList<Position> positions;
    private int[] boundingBox;

    Face(Rect rect) {
        setBoundingBox(rect);
        this.positions = new ArrayList<>();
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
        this.positions = new ArrayList<>(positions);
    }

    public ArrayList<Position> getPositions() {
        return this.positions;
    }

    public android.graphics.Rect getRectAndroid() {
        int r, t, l, b;
        r = this.rect.x;
        t = this.rect.y;
        l = this.rect.width  + r;
        b = this.rect.height + t;
        return new android.graphics.Rect(l, t, r, b);
    }

    public void addPosition(int x, int y) {
        this.positions.add(new Position(x, y));
    }

    private ArrayList<Position> subList(int start, int end) {
        return new ArrayList<Position>(this.positions.subList(start, end));
    }

    public ArrayList<Position> getChin() {
        return subList(0, 17);
    }

    public ArrayList<Position> getRightEyeBrow() {
        return subList(17, 22);
    }

    public ArrayList<Position> getLeftEyeBrow() {
        return subList(22, 27);
    }

    public ArrayList<Position> getNose() {
        return subList(28, 36);
    }

    public ArrayList<Position> getRightEye() {
        return subList(36, 42);
    }

    public ArrayList<Position> getLeftEye() {
        return subList(42, 48);
    }

    public ArrayList<Position> getMouth() {
        return subList(49, 60);
    }

}
