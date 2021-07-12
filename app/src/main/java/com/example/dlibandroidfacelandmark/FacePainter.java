package com.example.dlibandroidfacelandmark;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

public class FacePainter {
    private Canvas canvas;
    private Paint paint;
    private Bitmap bitmap;
    private int radius;

    FacePainter() {
        canvas = new Canvas();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        this.radius = 5;
    }

    public FacePainter setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap.copy( Bitmap.Config.ARGB_8888, true);
        canvas.setBitmap(this.bitmap);
        return this;
    }

    public void drawCircle(float x, float y) {
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

    public void drawPosition(Position p) {
        drawCircle(
                (float) p.getX(),
                (float) p.getY()
        );
    }

    public void drawPositions(ArrayList<Position> positions) {
        for (Position position: positions)
            drawPosition(position);
    }

    public void drawPositions(Face face) {
        drawPositions(face.getPositions());
    }

    public void drawFaces(ArrayList<Face> faces) {
        this.clearCanvas();
        for (Face face: faces)
            drawPositions(face);
    }

    public void clearCanvas() {
        this.canvas.drawColor(Color.TRANSPARENT);
    }
}
