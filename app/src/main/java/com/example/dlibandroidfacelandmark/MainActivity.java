package com.example.dlibandroidfacelandmark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dlibandroidfacelandmark.databinding.ActivityMainBinding;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.controls.Facing;

import java.io.IOException;
import java.io.InputStream;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startCamera();

        getFilesDir();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    private void startCamera() {
        camera  = (CameraView) findViewById(R.id.camera);
        overLay = (ImageView)  findViewById(R.id.cameraOverLay);
        camera.setLifecycleOwner(this);
        setupFramProcessor();
        facingButtonClickListener();
    }

    private void setupFramProcessor() {
        String fileName = "shape_predictor_68_face_landmarks_GTX.dat";
        camera.addFrameProcessor(new DLibFrameProcessor(
                overLay,
                new DLibResult(getAssets(), fileName)
        ));
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


}