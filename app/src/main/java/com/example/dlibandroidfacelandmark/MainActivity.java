package com.example.dlibandroidfacelandmark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dlibandroidfacelandmark.databinding.ActivityMainBinding;

import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.size.Size;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private ActivityMainBinding binding;
    private CameraView camera;
    private boolean facing = true;
    private Button facingButton;
    private ImageView overLay;
    private final String TAG = "MainActivity";
    private final String shape_pred_file = "shape_predictor_68_face_landmarks_GTX.dat";
    private DLibResult dLibResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getFilesDir();

        startCamera();
    }

    private void startCamera() {
        camera  = (CameraView) findViewById(R.id.camera);
        overLay = (ImageView)  findViewById(R.id.cameraOverLay);
        dLibResult = new DLibResult(getAssets(), shape_pred_file);
        camera.setLifecycleOwner(this);
        setupFramProcessor();
        facingButtonClickListener();
    }

    private void fullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    private void setupFramProcessor() {
        camera.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull @NotNull Frame frame) {
                dlibFrameProcessor(frame);
            }
        });
    }

    private void facingButtonClickListener() {
        facingButton = (Button) findViewById(R.id.cameraChange);
        facingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFacing();
            }
        });
    }

    private void changeFacing() {
        camera.setFacing(facing ? Facing.FRONT : Facing.BACK);
        facing = !facing;
    }

    private void dlibFrameProcessor(@NonNull @NotNull Frame frame) {
        Bitmap image = frame2Bitmap(frame, 80);
        dLibResult.processFrame(image);
    }

    private Bitmap frame2Bitmap(@NonNull @NotNull Frame frame, int quality) {
        byte[] data = frame.getData();
        Size size  = frame.getSize();
        int h = size.getHeight();
        int w = size.getWidth();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, w, h, null);
        yuvImage.compressToJpeg(new Rect(0, 0, w, h), quality, out);
        byte[] imageBytes = out.toByteArray();

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    private void drawLandmarks(Bitmap image) {
        Bitmap bitmap = image.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint  paint  = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        for (Position p: dLibResult.getPositions())
            canvas.drawCircle(
                    (float) p.getX(),
                    (float) p.getY(),
                    1,
                    paint
            );
        this.overLay.setImageBitmap(bitmap);
    }
}