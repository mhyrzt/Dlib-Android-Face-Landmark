package com.example.dlibandroidfacelandmark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edt_face_activity);
        setup();
    }

    private void setup() {
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

    private void setupFacePaint() {
        imageView = (ImageView) findViewById(R.id.imageView);
        Bitmap image;
        ArrayList<Face> faces;
        image = GlobalVars.image;
        faces = GlobalVars.faces;
        this.facePainter = new FacePainter();
        facePainter.setBitmap(image);
        updateImageView();
    }

    private void updateImageView() {
        imageView.setImageBitmap(this.facePainter.getBitmap());
    }

    private void setChangeListener(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateValue(seekBar, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void updateValue(SeekBar seekBar, int progress) {
        String name = "";
        switch (seekBar.getId()) {
            case R.id.redSeekBar:
                name = "r";
                vr = progress;
                break;
            case R.id.greenSeekbar:
                name = "g";
                vg = progress;
                break;
            case R.id.blueSeekbar:
                name = "b";
                vb = progress;
                break;
            case R.id.alphaSeekbar:
                name = "a";
                va = progress;
                break;
        }

        String log = name.toUpperCase() + "\n" + progress + "\n" + String.format("(%d, %d, %d, %d)", vr, vg, vb, va);
        ((TextView) findViewById(R.id.test)).setText(log);
    }
}