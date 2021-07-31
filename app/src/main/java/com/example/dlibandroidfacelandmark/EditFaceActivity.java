package com.example.dlibandroidfacelandmark;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class EditFaceActivity extends AppCompatActivity {
    private SeekBar r, g, b, a;
    private int vr, vg, vb, va;
    private static final String TAG = "EditFaceActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edt_face_activity);
        setup();
    }

    private void setup() {
        r = (SeekBar) findViewById(R.id.redSeekBar);
        g = (SeekBar) findViewById(R.id.greenSeekbar);
        b = (SeekBar) findViewById(R.id.blueSeekbar);
        a = (SeekBar) findViewById(R.id.alphaSeekbar);
        setChangeListener(r);
        setChangeListener(g);
        setChangeListener(b);
        setChangeListener(a);
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