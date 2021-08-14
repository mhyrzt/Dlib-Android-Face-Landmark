package com.example.dlibandroidfacelandmark;

import static org.opencv.core.CvType.CV_8UC1;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Face {
    private static final String TAG = "Face";
    private Rect rect;
    private ArrayList<Position> positions;
    private int[] boundingBox;
    private Mat mask;
    private ArrayList<Position> lipStick;

    Face(Rect rect) {
        setBoundingBox(rect);
        this.positions = new ArrayList<>();
    }

    Face(Rect rect, Mat mask) {
        this(rect);
        this.mask = mask;
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
        return subList(48, 60);
    }

    public Mat getMask() {
        return mask;
    }

    private MatOfPoint getMatPoints(ArrayList<Position> positions) {
        MatOfPoint matOfPoint = new MatOfPoint();
        List<Point> points = new ArrayList<>();
        for (Position position: positions)
            points.add(new Point(position.getX(), position.getY()));
        matOfPoint.fromList(points);
        return matOfPoint;
    }

    private void setMask(ArrayList<Position> positions, Size size) {
        Mat mask = new Mat(size, CV_8UC1);

        Imgproc.fillConvexPoly(
                mask,
                getMatPoints(positions),
                new Scalar(255, 255, 255)
        );
        Mat kernel = Imgproc.getStructuringElement(
                Imgproc.MORPH_RECT,
                new Size(40, 40)
        );

        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);
        Imgproc.GaussianBlur(mask, mask, new Size(15, 15), Core.BORDER_DEFAULT);

        this.setLipStick(mask);
    }

    private boolean isMask(Mat mask, int r, int c) {
        return mask.get(r, c)[0] == 255;
    }

    private void setLipStick(Mat mask) {
        this.lipStick = new ArrayList<>();
        for (int c = 0; c < (int) mask.size().width; c++)
            for (int r = 0; r < (int) mask.size().height; r++)
                if (isMask(mask, r, c))
                    lipStick.add(new Position(c, r));
    }

    public void setLipsMask(Size size) {
        this.setMask(this.getMouth(), size);
    }

    public ArrayList<Position> getLipStick() {
        return this.lipStick;
    }

}
