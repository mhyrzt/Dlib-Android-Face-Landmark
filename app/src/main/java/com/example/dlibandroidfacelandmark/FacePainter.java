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
                (float) p.getX(),
                (float) p.getY()
        );
    }

    private void drawPixel(Position p) {
        int x = (int) p.getX();
        int y = (int) p.getY();
        this.canvas.drawCircle(x, y, 1, this.paint);
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
                (float) positions.get(0).getX(),
                (float) positions.get(0).getY()
        );
        for (int i = 1; i < positions.size(); i++)
            path.lineTo(
                    (float) positions.get(i).getX(),
                    (float) positions.get(i).getY()
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

    public void drawLipStick(Face face, int rgba) {
        this.paint.setColor(rgba);
        for (Position p: face.getLipStick()) {
            this.drawPixel(p);
        }
    }
}
