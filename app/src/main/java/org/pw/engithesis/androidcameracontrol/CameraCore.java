package org.pw.engithesis.androidcameracontrol;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.pw.engithesis.androidcameracontrol.facedetectors.FaceDetector;
import org.pw.engithesis.androidcameracontrol.facedetectors.LbpCascadeFaceDetector;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CameraCore {

    private final AppCompatActivity activity;
    private final ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    ImageProxyToMatConverter proxyConverter = new ImageProxyToMatConverter();
    private ImageView imgView = null;
    private FaceDetector faceDetector;
    private FPSCounter fpsCounter;
    private FacemarkDetector facemarkLBF;


    public CameraCore(AppCompatActivity context) {
        OpenCVLoader.initDebug();
        activity = context;
        cameraProviderFuture = ProcessCameraProvider.getInstance(activity);
        faceDetector = new LbpCascadeFaceDetector();
        fpsCounter = new FPSCounter();

        facemarkLBF = new FacemarkDetector();
    }

    /*
        Finalnie nie bedzie wyswietlania - wtedy usunac trzeba
        Wyswietlanie tylko do testow
     */
    public void setImageView(ImageView imageView) {
        imgView = imageView;
    }

    public FPSCounter getFpsCounter() {
        return fpsCounter;
    }

    public void start() {
        cameraProviderFuture.addListener(prepareCameraProviderRunnable(), ContextCompat.getMainExecutor(activity));
    }

    private void bindImageAnalysis(ProcessCameraProvider cameraProvider) {
        ImageAnalysis imageAnalysis = prepareImageAnalysis();
        CameraSelector cameraSelector = prepareCameraSelector();

        cameraProvider.bindToLifecycle((LifecycleOwner) activity, cameraSelector, imageAnalysis);
    }

    private Runnable prepareCameraProviderRunnable() {
        return () -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindImageAnalysis(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        };
    }


    private CameraSelector prepareCameraSelector() {
        return new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();
    }

    private ImageAnalysis prepareImageAnalysis() {
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(activity), prepareAnalyzer());

        return imageAnalysis;
    }


    private ImageAnalysis.Analyzer prepareAnalyzer() {
        return image -> {
            if (fpsCounter != null) {
                fpsCounter.tick();
            }

            proxyConverter.setFrame(image);
            Mat mat = proxyConverter.rgb();
            MatOfRect faces = faceDetector.detect(mat);

            ArrayList<MatOfPoint2f> landmarks = facemarkLBF.detect(mat, faces);
            facemarkLBF.drawLandmarks(mat, landmarks);


            faceDetector.drawFaceSquare(mat, faces);


            if (imgView != null) {
                Bitmap bitmap = proxyConverter.createBitmap(mat);
                imgView.setImageBitmap(bitmap);
            }
            image.close();
        };
    }
}
