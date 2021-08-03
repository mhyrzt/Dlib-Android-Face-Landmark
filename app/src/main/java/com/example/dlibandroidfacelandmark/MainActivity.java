package com.example.dlibandroidfacelandmark;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dlibandroidfacelandmark.databinding.ActivityMainBinding;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Mode;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    private ActivityMainBinding binding;
    private final String TAG = "MainActivity";
    private final Handler handler = new Handler();
    private boolean facing = true;
    private ImageView processingResult;
    private CameraView camera;
    private DLibResult dLibResult;
    private FacePainter facePainter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getFilesDir();
        dLibResult = new DLibResult(
                this,
                "shape_predictor_68_face_landmarks_GTX.dat"
        );
        startCamera();
        facePainter = new FacePainter();
    }

    private void startCamera() {
        processingResult = findViewById(R.id.resultProcess);
        camera = findViewById(R.id.camera);
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
        findViewById(R.id.cameraChange)
                .setOnClickListener(v -> changeFacing());
    }

    private void setupCamerClickListener() {
        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull @NotNull PictureResult result) {
                super.onPictureTaken(result);

                result.toBitmap(4096, 4096, bitmap -> {
                    if (null == bitmap) return;
                    // showResultLayOut(processLandmarks(bitmap));
                    navigateToFaceEditActivity(bitmap);
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
        findViewById(R.id.btnCapture)
                .setOnClickListener(v -> camera.takePicture());
    }

    private void changeFacing() {
        camera.setFacing(facing ? Facing.FRONT : Facing.BACK);
        facing = !facing;
    }

    private void setFrameProcessor() {
        this.camera.setFrameProcessingExecutors(4);
        this.camera.setFrameProcessingPoolSize(5);
        this.camera.addFrameProcessor(frame -> { });
    }

    private Bitmap processLandmarks(Bitmap image) {
        dLibResult.processFrame(image);
        facePainter
                .setBitmap(image)
                .drawFacesLandMarks(dLibResult.getFaces());
        return facePainter.getBitmap();
    }

    private void showResultLayOut(Bitmap bitmap) {
        processingResult.setImageResource(0);
        processingResult.setImageBitmap(bitmap);
    }

    private void clearButtonClickListener() {
        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v ->
                processingResult.setImageResource(0)
        );
    }

    private void navigateToFaceEditActivity(Bitmap bitmap) {
        this.dLibResult.processFrame(bitmap);
        GlobalVars.image = bitmap;
        GlobalVars.faces = this.dLibResult.getFaces();
        Intent intent = new Intent(MainActivity.this, EditFaceActivity.class);
        startActivity(intent);
    }

}