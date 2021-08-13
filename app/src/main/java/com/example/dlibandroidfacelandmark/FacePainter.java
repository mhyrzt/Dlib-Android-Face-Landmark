package com.example.dlibandroidfacelandmark;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC4;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

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
    private static final String TAG = "FacePainter";
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

    private Mat getMask(ArrayList<Position> positions) {
        Size size = new Size(
                this.bitmap.getWidth(),
                this.bitmap.getHeight()
        );

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
                mask,
                Imgproc.MORPH_CLOSE,
                kernel
        );

        Imgproc.GaussianBlur(
                mask,
                mask,
                new Size(15, 15),
                Core.BORDER_DEFAULT
        );

        return mask;
    }

    private Bitmap mat2Bmp(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(
                mat.cols(),
                mat.rows(),
                Bitmap.Config.ARGB_8888
        );
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    private void replaceMaskColor(Bitmap bitmap, int argb) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                int pixel = bitmap.getPixel(c, r);
                if (
                        Color.red(pixel) == 255
                        && Color.blue(pixel) == 255
                        && Color.green(pixel) == 255
                        && Color.alpha(pixel) == 1
                ) {
                    bitmap.setPixel(c, r, argb);
                } else {
                    bitmap.setPixel(c, r, 0);
                }
            }
        }
    }

    public void drawMask(ArrayList<Position> positions, int color) {
        Bitmap mask = mat2Bmp(getMask(positions));
        replaceMaskColor(mask, color);
        canvas.setBitmap(mask);
    }
}
