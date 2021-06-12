package com.example.dlibandroidfacelandmark;

import java.util.ArrayList;


public class DLibResult {
    private ArrayList<Face> faces;
    private long count;

    DLibResult() {
        faces = new ArrayList<Face>();
        count = 0;
    }

    public void addFace(Face face) {
        faces.add(face);
        count++;
    }

    public ArrayList<Face> getFaces() {
        return this.faces;
    }

    public long getCount() {
        return this.count;
    }
}
