package com.example.dlibandroidfacelandmark;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EditFaceActivity extends AppCompatActivity {
    private SeekBar r, g, b, a;
    private int vr, vg, vb, va;

    private FacePainter facePainter;
    private Bitmap image;
    private ArrayList<Face> faces;

    private ImageView imageView;
    private CheckBox drawLandmarksCheckbox;
    private boolean drawLandmarks = false;

    private CheckBox drawBoundingBoxCheckBox;
    private boolean boundingBox = false;
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
        this.facePainter = new FacePainter(this.image);
        updateImageView();
    }

    private void updateImageView() {
        resetImageView();
        drawLips();
        handleCheckBoxes();
    }

    private void resetImageView() {
        this.facePainter.clearCanvas();
        this.facePainter.setBitmap(this.image);
        imageView.setImageBitmap(this.facePainter.getBitmap());
    }

    private void handleCheckBoxes() {
        if (drawLandmarks)
            this.facePainter.drawFacesLandMarks(this.faces);

        if (boundingBox)
            this.facePainter.drawFacesBoundingBox(this.faces);
    }

    private void drawLips() {
        int color = Color.argb(va, vr, vg, vb);
        try {
            for (Face face: this.faces)
                facePainter.drawLipStick(face, color);
        } catch (Exception e) {
            makeShortToast("TRY AGAIN!");
            finish();
        }
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
        updateImageView();
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
            try {
                saveImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    private File makePath() {
        String storePath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "dlibApp";

        File appDir = new File(storePath);
        if (!appDir.exists())
            appDir.mkdir();

        return appDir;
    }

    private File makeFile(File appDir) {
        String fileName = "dlibApp_result_" + System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        return file;
    }

    private boolean compressBitmap(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        boolean success = this.facePainter
                .getBitmap()
                .compress(
                        Bitmap.CompressFormat.JPEG,
                        60,
                        fos
                );
        fos.flush();
        fos.close();
        return success;
    }

    private void makeShortToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void saveImage() throws IOException {
        requestStoragePermission();

        File appDir = makePath();
        File file = makeFile(appDir);
        if (compressBitmap(file))
            makeShortToast("Image Saved Successfully!");
        else
            makeShortToast("ERROR!");
    }

    private boolean hasWritePermission() {
        return ContextCompat
                .checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        if (!hasWritePermission()) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    1
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (
                    grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) { }
            else { }
        }
    }
}