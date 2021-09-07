package org.pw.engithesis.androidcameracontrol;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.pw.engithesis.androidcameracontrol.interfaces.FPSView;

public class CameraX_test extends AppCompatActivity implements FPSView {
    ImageView imgView;
    CameraCore cameraCore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("CAMERAX_TEST", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_image_view);
        imgView = (ImageView) findViewById(R.id.camera_preview);
        cameraCore = new CameraCore(this);
        cameraCore.setImageView(imgView);
        cameraCore.setFpsView(this);
        cameraCore.start();
    }

    public void setFPSText(double curFPS, double avgFPS) {
        TextView curFPSTextView = (TextView) findViewById(R.id.cur_fps_text);
        TextView avgFPSTextView = (TextView) findViewById(R.id.avg_fps_text);

        curFPSTextView.setText(String.format("%.2f", curFPS));
        avgFPSTextView.setText(String.format("%.2f", avgFPS));

    }
}