package com.example.dlibandroidfacelandmark;

import android.media.Image;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.size.Size;

import org.jetbrains.annotations.NotNull;

public class DLibFrameProcessor implements FrameProcessor {

    private ImageView imageView;
    private DLibResult dLibResult;
    private final String TAG = "DLibFrameProcessor";

    DLibFrameProcessor(ImageView imageView, DLibResult dLibResult) {
        this.dLibResult = dLibResult;
        this.imageView  = imageView;
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
//        dLibResult.processFrame(yuv, w, h);
        Log.v(TAG, "process: Frame Processed");
    }
}
