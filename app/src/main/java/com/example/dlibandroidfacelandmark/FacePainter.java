package com.example.dlibandroidfacelandmark;

import static org.opencv.core.CvType.CV_8UC1;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class FacePainter {
    private Canvas canvas;
    private Paint paint;
    private Bitmap bitmap;

    private int radius;

    FacePainter() {
        this.radius = 5;
        this.canvas = new Canvas();
        this.paint  = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paint.setColor(Color.RED);
    }

    public FacePainter setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap.copy( Bitmap.Config.ARGB_8888, true);
        canvas.setBitmap(this.bitmap);
        return this;
    }

    private void drawCircle(float x, float y) {
        this.canvas.drawCircle(x, y, this.radius, this.paint);
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setColor(int color) {
        this.paint.setColor(color);
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    private void drawPosition(Position p) {
        drawCircle(
                (float) p.getX() * 2,
                (float) p.getY() * 2
        );
    }

    private void drawPositions(ArrayList<Position> positions) {
        paint.setStyle(Paint.Style.FILL);
        for (Position position: positions)
            drawPosition(position);
    }

    private void drawPositions(Face face) {
        drawPositions(face.getPositions());
    }

    public FacePainter drawFacesLandMarks(ArrayList<Face> faces) {
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(this.radius);
        this.paint.setColor(Color.RED);
        for (Face face: faces)
            drawPositions(face);
        return this;
    }

    private void drawRectangle(Face face) {
        this.canvas.drawRect(
                face.getRectAndroid(),
                this.paint
        );
    }

    public FacePainter drawFacesBoundingBox(ArrayList<Face> faces) {
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(this.radius);
        this.paint.setColor(Color.GREEN);

        for (Face face: faces)
            this.drawRectangle(face);
        return this;
    }

    public void drawPolygon(ArrayList<Position> positions, int color) {
        this.paint.setColor(color);
        this.paint.setStyle(Paint.Style.FILL);

        Path path = new Path();
        path.moveTo(
                (float) positions.get(0).getX() * 2,
                (float) positions.get(0).getY() * 2
        );
        for (int i = 1; i < positions.size(); i++)
            path.lineTo(
                    (float) positions.get(i).getX() * 2,
                    (float) positions.get(i).getY() * 2
            );
        path.close();

        this.canvas.drawPath(path, this.paint);
    }

    public void drawPolygon(ArrayList<Position> positions) {
        this.drawPolygon(positions, this.paint.getColor());
    }

    public void clearCanvas() {
        this.canvas.drawColor(Color.BLACK);
    }

    public static int getRGBA(int r, int g, int b, int a) {
        a = (a & 0xff) << 24;
        r = (r & 0xff) << 16;
        g = (g & 0xff) << 8;
        b = (b & 0xff);
        return r | g | b | a;
    }

    private MatOfPoint getMatPoints(ArrayList<Position> positions) {
        MatOfPoint matOfPoint = new MatOfPoint();
        List<Point> points = new ArrayList<>();
        for (Position position: positions)
            points.add(new Point(position.getX(), position.getY()));
        matOfPoint.fromList(points);
        return matOfPoint;
    }


    private Mat applyMaskToMat(Mat mask) {
        Mat image = bitmap2Mat();
        Mat matResult = new Mat();
        Core.multiply(mask, image, matResult);
        return matResult;
    }

    private Mat bitmap2Mat() {
        Mat image = new Mat();
        Utils.bitmapToMat(this.bitmap, image);
        return image;
    }

    private Mat getMask(ArrayList<Position> positions) {
        Size size = new Size(this.bitmap.getWidth(), this.bitmap.getHeight());

        Mat temp = new Mat();

        Mat mask = Mat.zeros(
               size,
                CV_8UC1
        );

        Imgproc.fillConvexPoly(
                mask,
                getMatPoints(positions),
                new Scalar(255, 255, 255)
        );

        Mat kernel = Imgproc.getStructuringElement(
                Imgproc.MORPH_RECT,
                new Size(40, 40)
        );

        Imgproc.morphologyEx(
                mask,
                temp,
                Imgproc.MORPH_CLOSE,
                kernel
        );

        Imgproc.GaussianBlur(
                temp,
                mask,
                new Size(15, 15),
                Core.BORDER_DEFAULT
        );

        return mask;
    }

    private Mat getInvMask(ArrayList<Position> positions){
        Mat mask = getMask(positions);
        Mat invMask = new Mat();
        Core.bitwise_not(mask, invMask);
        return invMask;
    }

    private Mat applyInvMask(ArrayList<Position> positions){
        Mat invMask = getInvMask(positions);
        return applyMaskToMat(invMask);
    }

    private Mat applyMask(ArrayList<Position> positions) {
        Mat mask  = getMask(positions);
        return applyMaskToMat(mask);
    }

    public Bitmap applyMaskColor(ArrayList<Position> positions, int r, int g, int b, int a) {
        Mat mask  = applyMask(positions);
        Scalar color = new Scalar(b, g, r, a);
        Mat out_mask = new Mat();
        Core.multiply(mask, color, out_mask);

        Mat invm  = applyInvMask(positions);
        Mat image = new Mat();
        Mat out_invm = new Mat();
        Utils.bitmapToMat(this.bitmap, image);
        Core.multiply(invm, image, out_invm);

        Mat result = new Mat();
        Core.add(out_invm, out_mask, result);

        Bitmap ans = Bitmap.createBitmap(this.bitmap);
        Utils.matToBitmap(result, ans);

        return ans;
    }

}
