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

    DLibResult(Context AppContext, String fileName) {
        positions = new ArrayList<Position>();
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

    private int[] detectBB(Bitmap image) {
        ArrayList<Rect> rects = faceDetector.detectFaces(image);
        int[] bb = new int[rects.size() * 4];

        for (int i = 0; i < rects.size(); i++) {
            int j = i * 4;
            Log.d(TAG, "detectBB: " + rects.get(i));
            bb[j++] = rects.get(i).x;
            bb[j++] = rects.get(i).y;
            bb[j++] = rects.get(i).width;
            bb[j]   = rects.get(i).height;
        }
        return bb;
    }
    public void processFrame(Bitmap image) {
        processLandMarks(image, detectBB(image));
    }
    private native void setupDlib(AssetManager assetManager, String fileName);
    private native void processLandMarks(Bitmap bitmap, int[] bb);

}
