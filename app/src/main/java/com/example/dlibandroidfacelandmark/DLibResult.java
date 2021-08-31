package com.example.dlibandroidfacelandmark;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.Rect;
import org.opencv.core.Size;

import java.util.ArrayList;

public class DLibResult {

    static {
        System.loadLibrary("dlib_face_result");
    }

    private ArrayList<Position> positions;
    private FaceDetectorOpenCv faceDetector;
    private static final String TAG = "DLibResult";
    private ArrayList<Face> faces;

    DLibResult(Context AppContext, String fileName) {
        positions  = new ArrayList<>();
        this.faces = new ArrayList<>();
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
        faces.clear();
        Size size = new Size(image.getWidth(), image.getHeight());

        for (Rect r: faceDetector.detectFaces(image)) {
            this.positions.clear();

            Face face = new Face(r);
            processLandMarks(image, face.getBoundingBox());
            face.setPositions(this.positions);
            face.setLipsMask(size);

            faces.add(face);
            positions.clear();
        }
    }

    public ArrayList<Face> getFaces() {
        return faces;
    }

    public void processFrame(Bitmap image) {
        detectFaces(image);
    }

    private native void setupDlib(AssetManager assetManager, String fileName);
    private native void processLandMarks(Bitmap bitmap, int[] bb);
}
