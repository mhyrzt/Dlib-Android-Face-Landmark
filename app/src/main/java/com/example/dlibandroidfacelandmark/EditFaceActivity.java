package com.example.dlibandroidfacelandmark;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;

public class EditFaceActivity extends AppCompatActivity {
    private SeekBar r, g, b, a;
    private int vr, vg, vb, va;
    private static final String TAG = "EditFaceActivity";

    private FacePainter facePainter;
    private Bitmap image;
    private ArrayList<Face> faces;

    private ImageView imageView;
    private CheckBox drawLandmarksCheckbox; private boolean drawLandmarks = false;
    private CheckBox drawBoundingBoxCheckBox; private boolean boundingBox = false;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edt_face_activity);
        setup();
    }

    private void setup() {
        setupWidgets();
        setupSeekBars();
        setupFacePaint();
    }

    private void setupSeekBars() {
        r = (SeekBar) findViewById(R.id.redSeekBar);
        b = (SeekBar) findViewById(R.id.blueSeekbar);
        g = (SeekBar) findViewById(R.id.greenSeekbar);
        a = (SeekBar) findViewById(R.id.alphaSeekbar);
        setChangeListener(r);
        setChangeListener(g);
        setChangeListener(b);
        setChangeListener(a);
    }

    private void setupWidgets() {
        saveBtn     = (Button) findViewById(R.id.saveBtn);
        imageView   = (ImageView) findViewById(R.id.imageView);
        drawLandmarksCheckbox   = (CheckBox) findViewById(R.id.drawLandmarksCheckBox);
        drawBoundingBoxCheckBox = (CheckBox) findViewById(R.id.boundingBoxCheckBox);
        setupListeners();
    }

    private void setupFacePaint() {
        this.image = GlobalVars.image;
        this.faces = GlobalVars.faces;
        this.facePainter = new FacePainter();
        facePainter.setBitmap(this.image);
        updateImageView();
    }

    private void updateImageView() {
        this.facePainter.clearCanvas();
        imageView.setImageBitmap(this.facePainter.getBitmap());

        if (drawLandmarks)
            this.facePainter.drawFacesLandMarks(this.faces);
        if (boundingBox)
            this.facePainter.drawFacesBoundingBox(this.faces);
    }

    private void setChangeListener(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateValue(seekBar, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateValue(SeekBar seekBar, int progress) {
        switch (seekBar.getId()) {
            case R.id.redSeekBar:
                vr = progress;
                break;
            case R.id.greenSeekbar:
                vg = progress;
                break;
            case R.id.blueSeekbar:
                vb = progress;
                break;
            case R.id.alphaSeekbar:
                va = progress;
                break;
        }
    }

    private void setupListeners() {
        setupSaveButton();
        setupDrawBoundinBoxCheckBox();
        setupDrawLandmarksCheckBoxListener();
    }

    private void setupDrawLandmarksCheckBoxListener() {
        this.drawLandmarksCheckbox.setOnClickListener(v -> {
            drawLandmarks = drawLandmarksCheckbox.isChecked();
            updateImageView();
        });
    }

    private void setupDrawBoundinBoxCheckBox() {
        this.drawBoundingBoxCheckBox.setOnClickListener(v -> {
            boundingBox = drawBoundingBoxCheckBox.isChecked();
            updateImageView();
        });
    }

    private void setupSaveButton() {
        this.saveBtn.setOnClickListener(v -> {
            saveImage();
        });
    }

    private void saveImage() {
        Toast.makeText(this, "image saved", Toast.LENGTH_SHORT).show();
    }
}