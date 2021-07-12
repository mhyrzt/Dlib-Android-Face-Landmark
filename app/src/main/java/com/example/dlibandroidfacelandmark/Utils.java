package com.example.dlibandroidfacelandmark;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.size.Size;

import java.io.ByteArrayOutputStream;

public class Utils {
    public static Bitmap conv2Nv21(Frame frame) {
        byte[] data = frame.getData();
        Size size   = frame.getSize();

        int h = size.getHeight();
        int w = size.getWidth();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, w, h, null);
        yuvImage.compressToJpeg(new Rect(0, 0, w, h), 80, out);
        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

}
