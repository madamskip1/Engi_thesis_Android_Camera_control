package org.pw.engithesis.androidcameracontrol;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.pw.engithesis.androidcameracontrol.tests.SelectImageTest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AndroidCameraControl_NoTitleBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void goLiveFront(View view) {
        Intent goLiveFront = new Intent(this, CameraX_test.class);
        startActivity(goLiveFront);
    }

    public void goSelectImageTest(View view) {
        Intent selectImageTestIntent = new Intent(this, SelectImageTest.class);
        startActivity(selectImageTestIntent);
    }

    public void goImageSlider(View view) {
        Intent imageSliderIntent = new Intent(this, ImageSlider.class);
        startActivity(imageSliderIntent);
    }

    public void goBlink(View view) {
        Intent blinkIntent = new Intent(this, BlinkActivity.class);
        startActivity(blinkIntent);
    }
}