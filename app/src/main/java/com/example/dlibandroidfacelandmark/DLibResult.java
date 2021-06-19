package com.example.dlibandroidfacelandmark;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.InputStream;
import java.util.ArrayList;


public class DLibResult {

    static {
        System.loadLibrary("dlib_face_result");
    }

    private ArrayList<Position> positions;

    DLibResult(AssetManager assetManager, String fileName) {
        positions = new ArrayList<Position>();
        setupDlib(assetManager, fileName);
    }

    public void addPosition(int x, int y) {
        Position position = new Position(x, y);
        positions.add(position);
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    private native void setupDlib(AssetManager assetManager, String fileName);
    public native void processFrame(Bitmap bitmap);
}
