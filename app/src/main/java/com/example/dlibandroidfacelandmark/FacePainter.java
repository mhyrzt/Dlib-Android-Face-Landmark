package com.example.dlibandroidfacelandmark;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

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

    FacePainter(Bitmap bitmap) {
        this();
        setBitmap(bitmap);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvas.setBitmap(this.bitmap);
    }

    private void drawCircle(double x, double y) {
        this.canvas.drawCircle((float) x, (float) y, this.radius, this.paint);
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
        double x = p.getX();
        double y = p.getY();
        drawCircle(x, y);
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

    public void clearCanvas() {
        this.canvas.drawColor(Color.BLACK);
    }

    public void drawLipStick(Face face, int rgba) {
        drawPolygon(face.getLipStick(), rgba);
    }
}
