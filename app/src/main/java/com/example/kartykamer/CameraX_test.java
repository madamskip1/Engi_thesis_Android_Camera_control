package com.example.kartykamer;

import androidx.annotation.NonNull;
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
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

public class CameraX_test extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private CameraXToOpenCV cameraXToOpenCV;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_image_view);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        textView = (TextView) findViewById(R.id.orientation);
        cameraXToOpenCV = new CameraXToOpenCV();

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindImageAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }


    private void bindImageAnalysis(ProcessCameraProvider cameraProvider) {
        ImageAnalysis imageAnalyzer = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalyzer.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                int degrees = image.getImageInfo().getRotationDegrees();

                cameraXToOpenCV.setFrame(image);

                Bitmap bitmap = cameraXToOpenCV.grayBitmap();
                Log.d("DUPA", "Utworzyono bitmap");

                ImageView imgView = (ImageView) findViewById(R.id.camera_preview);
                //imgView.setRotation((float) 270);
                imgView.setImageBitmap(bitmap);
                image.close();
            }
        });

        OrientationEventListener orientationListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                textView.setText(Integer.toString(orientation));
                int surfaceRotation = -1;

                if (orientation >= 315 || orientation < 45)
                    surfaceRotation = Surface.ROTATION_0;
                else if (orientation >= 225 || orientation < 315)
                    surfaceRotation = Surface.ROTATION_90;
                else if (orientation >= 135 || orientation < 225)
                    surfaceRotation = Surface.ROTATION_180;
                else {
                    surfaceRotation = Surface.ROTATION_270;
                }
                Log.e("DUPA", "ROTATION SET: " + surfaceRotation);
                //imageAnalyzer.setTargetRotation(surfaceRotation);
            }
        };

        orientationListener.enable();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();


        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalyzer);
    }
}