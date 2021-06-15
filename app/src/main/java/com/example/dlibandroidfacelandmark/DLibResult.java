package com.example.dlibandroidfacelandmark;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.InputStream;
import java.util.ArrayList;


public class DLibResult {
    private ArrayList<Face> faces;
    private long count;
    static { System.loadLibrary("dlib_face_result"); }

    DLibResult(AssetManager assetManager, String fileName) {
        faces = new ArrayList<Face>();
        count = 0;
        setupDlib(assetManager, fileName);
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

    public void resetValues() {
        count = 0;
        faces.clear();
    }

    private native void setupDlib(AssetManager assetManager, String fileName);
    public native void processFrame(Bitmap bitmap);
}
