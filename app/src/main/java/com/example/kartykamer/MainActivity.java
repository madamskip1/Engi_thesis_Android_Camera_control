package com.example.kartykamer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
}