package com.example.dlibandroidfacelandmark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dlibandroidfacelandmark.databinding.ActivityMainBinding;

import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.size.Size;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    private ActivityMainBinding binding;
    private final String TAG = "MainActivity";
    private final Handler handler = new Handler();
    private final String shape_pred_file = "shape_predictor_68_face_landmarks_GTX.dat";
    private boolean facing = true;
    private ImageView processingResult;
    private CameraView camera;
    private DLibResult dLibResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getFilesDir();
        dLibResult = new DLibResult(this, shape_pred_file);
        startCamera();
    }

    private void startCamera() {
        processingResult = (ImageView) findViewById(R.id.resultProcess);
        camera = (CameraView) findViewById(R.id.camera);
        camera.setLifecycleOwner(this);

        camera.setMode(Mode.PICTURE);
        camera.setFacing(Facing.FRONT);
        facing = camera.getFacing() == Facing.BACK;
        setFrameProcessor();

        clearButtonClickListener();
        facingButtonClickListener();
        captureButtinClickListener();
    }

    private void facingButtonClickListener() {
        Button btnFacing = (Button) findViewById(R.id.cameraChange);
        btnFacing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFacing();
            }
        });
    }

    private Bitmap conv2NV21(Frame frame) {
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

    private void setupCamerClickListener() {
        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull @NotNull PictureResult result) {
                super.onPictureTaken(result);

                result.toBitmap(4096, 4096, new BitmapCallback() {
                    @Override
                    public void onBitmapReady(@Nullable @org.jetbrains.annotations.Nullable Bitmap bitmap) {
                        if (null == bitmap) return;
                        Bitmap result = processLandmarks(bitmap);
                        showResultLayOut(result);
                    }

                });
            }

            @Override
            public void onPictureShutter() {
                super.onPictureShutter();
            }
        });
    }

    private void captureButtinClickListener() {
        setupCamerClickListener();
        Button btnCapture = (Button) findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture();
            }
        });
    }

    private void changeFacing() {
        camera.setFacing(facing ? Facing.FRONT : Facing.BACK);
        facing = !facing;
    }

    private void setFrameProcessor() {
        this.camera.setFrameProcessingExecutors(4);
        this.camera.setFrameProcessingPoolSize(5);
        this.camera.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull @NotNull Frame frame) {
//                camera.takePicture();
            }
        });
    }

    private Bitmap processLandmarks(Bitmap image) {
        this.dLibResult.processFrame(image);
        Bitmap bitmap = image.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint  paint  = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        for (Position p: dLibResult.getPositions()){
            float x = (float) p.getX() * 2;
            float y = (float) p.getY() * 2;
            canvas.drawCircle(x, y, 10, paint);
        }
        return bitmap;
    }

    private void showResultLayOut(Bitmap bitmap) {
        processingResult.setImageResource(0);
        processingResult.setImageBitmap(bitmap);
    }

    private void clearButtonClickListener() {
        Button btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processingResult.setImageResource(0);
            }
        });
    }

}