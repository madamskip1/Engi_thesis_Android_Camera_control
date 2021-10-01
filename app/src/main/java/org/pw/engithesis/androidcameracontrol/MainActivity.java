package org.pw.engithesis.androidcameracontrol;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.pw.engithesis.androidcameracontrol.tests.SelectTest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AndroidCameraControl);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goChoiceFaceDetector(View view) {
        Intent selectFaceDetectorIntent = new Intent(this, SelectFaceDetector.class);
        startActivity(selectFaceDetectorIntent);
    }

    public void goCameraX(View view) {
        Intent goCameraX = new Intent(this, CameraX_test.class);
        startActivity(goCameraX);
    }

    public void goDrawable(View view) {
        Intent goDrawable = new Intent(this, DrawableTest.class);
        startActivity(goDrawable);
    }

    public void goSelectTest(View view) {
        Intent selectTestIntent = new Intent(this, SelectTest.class);
        startActivity(selectTestIntent);
    }
}