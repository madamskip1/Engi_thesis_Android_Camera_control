package com.example.kartykamer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kartykamer.interfaces.FPSView;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

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