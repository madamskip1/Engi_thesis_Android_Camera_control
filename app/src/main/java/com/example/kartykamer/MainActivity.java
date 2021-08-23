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

    public void goBackCamera(View view) {
        Intent backCameraIntent = new Intent(this, BackCamera.class);
        startActivity(backCameraIntent);
    }

    public void goTestNative(View view)
    {
        Intent nativeTest = new Intent(this, NativeTest.class);
        startActivity(nativeTest);
    }

}