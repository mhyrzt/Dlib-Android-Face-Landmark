package com.example.dlibandroidfacelandmark;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dlibandroidfacelandmark.databinding.ActivityMainBinding;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.controls.Facing;

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

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startCamera();
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
        camera.addFrameProcessor(new DLibFrameProcessor(overLay));
        facingButtonClickListener();
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