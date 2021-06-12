package com.example.dlibandroidfacelandmark;

import android.media.Image;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.size.Size;

import org.jetbrains.annotations.NotNull;

public class DLibFrameProcessor implements FrameProcessor {
//    static  {
//        System.loadLibrary();
//    }
    private ImageView imageView;
    DLibFrameProcessor(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void process(@NonNull @NotNull Frame frame) {
        // due to using enigne 1 are
        // .getData() always returns
        // data as byte[] with format
        // of NV21
        // https://natario1.github.io/CameraView/docs/frame-processing
        byte[] yuv = frame.getData();
        Size size  = frame.getSize();
        int h = size.getHeight();
        int w = size.getWidth();

    }
}
