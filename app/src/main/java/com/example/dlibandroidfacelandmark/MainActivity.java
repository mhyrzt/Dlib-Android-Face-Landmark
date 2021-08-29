package com.example.dlibandroidfacelandmark;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Mode;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity {

    private CameraView camera;
    private DLibResult dLibResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dLibResult = new DLibResult(
                this,
                "shape_predictor_68_face_landmarks_GTX.dat"
        );
        startCamera();
    }

    private void startCamera() {
        camera = findViewById(R.id.camera);
        camera.setLifecycleOwner(this);

        camera.setMode(Mode.PICTURE);
        camera.setFacing(Facing.FRONT);

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
        camera.setFacing(
                camera.getFacing() == Facing.BACK ?
                        Facing.FRONT :
                        Facing.BACK
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