package com.example.dlibandroidfacelandmark;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.opencv.core.Rect;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;


public class DLibResult {

    static {
        System.loadLibrary("dlib_face_result");
    }

    private ArrayList<Position> positions;
    private FaceDetectorOpenCv faceDetector;
    private static final String TAG = "DLibResult";
    private ArrayList<Face> faces;

    DLibResult(Context AppContext, String fileName) {
        positions  = new ArrayList<Position>();
        this.faces = new ArrayList<Face>();
        setupOpenCv(AppContext);
        setupDlib(AppContext.getAssets(), fileName);
    }

    public void addPosition(int x, int y) {
        Position position = new Position(x, y);
        positions.add(position);
    }

    private void setupOpenCv(Context AppContext) {
        try {
            faceDetector = new FaceDetectorOpenCv(AppContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    private void detectFaces(Bitmap image) {
        ArrayList<Rect> rects = faceDetector.detectFaces(image);
        for (Rect r: rects) {
            this.positions.clear();
            Face face = new Face(r);
            processLandMarks(image, face.getBoundingBox());
            face.setPositions(this.positions);
            this.faces.add(face);
        }
    }

    public ArrayList<Face> getFaces() {
        return faces;
    }

    public void processFrame(Bitmap image) {
        this.positions.clear();
        detectFaces(image);
    }

    private native void setupDlib(AssetManager assetManager, String fileName);
    private native void processLandMarks(Bitmap bitmap, int[] bb);

}
