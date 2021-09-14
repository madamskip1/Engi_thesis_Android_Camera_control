package org.pw.engithesis.androidcameracontrol;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.pw.engithesis.androidcameracontrol.interfaces.FPSView;


public class SelectFaceDetector extends AppCompatActivity implements FPSView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_face_detector);
    }

    public void goHaarCascade(View view) {
        setContentView(R.layout.camera_image_view);
        ImageView imgView;
        imgView = (ImageView) findViewById(R.id.camera_preview);
        CameraCore cameraCore = new CameraCore(this);
        cameraCore.setImageView(imgView);
     /*   cameraCore.setFpsView(this);*/
        cameraCore.start();
    }

    public void setFPSText(double curFPS, double avgFPS) {
        TextView curFPSTextView = (TextView) findViewById(R.id.cur_fps_text);
        TextView avgFPSTextView = (TextView) findViewById(R.id.avg_fps_text);

        curFPSTextView.setText(String.format("%.2f", curFPS));
        avgFPSTextView.setText(String.format("%.2f", avgFPS));

    }
}