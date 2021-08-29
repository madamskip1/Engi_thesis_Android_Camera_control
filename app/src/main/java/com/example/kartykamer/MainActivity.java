package com.example.kartykamer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void goTestNative(View view) {
        Intent nativeTest = new Intent(this, NativeTest.class);
        startActivity(nativeTest);
    }

    public void goChoiceFaceDetector(View view) {
        Intent selectFaceDetectorIntent = new Intent(this, SelectFaceDetector.class);
        startActivity(selectFaceDetectorIntent);
    }

    public void goCamerX(View view) {
        Intent goCamerX = new Intent(this, CameraX_test.class);
        startActivity(goCamerX);
    }
}