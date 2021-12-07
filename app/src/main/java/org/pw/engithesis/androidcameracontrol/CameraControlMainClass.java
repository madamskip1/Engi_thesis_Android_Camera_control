package org.pw.engithesis.androidcameracontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.eyedetectionalgorithms.EyeDetectionFacemarks;
import org.pw.engithesis.androidcameracontrol.interfaces.Observer;

import java.util.concurrent.ExecutionException;

public class CameraControlMainClass {
    private final ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private final AppCompatActivity activity;
    private final FaceDetector faceDetector = new FaceDetector();
    private final FacemarksDetector facemarksDetector = new FacemarksDetector();
    private final EyeDetectionFacemarks eyeDetector = new EyeDetectionFacemarks();
    private final EyePupilDetector eyePupilDetector = new EyePupilDetector();
    private final EARClosedEyeDetector closedEyeDetector = new EARClosedEyeDetector();
    private final EyeMoveDetector eyeMoveDetector = new EyeMoveDetector();
    private final EyeBlinkDetector blinkDetector = new EyeBlinkDetector();
    ImageProxyToMatConverter proxyConverter = new ImageProxyToMatConverter();

    public CameraControlMainClass(AppCompatActivity activity) {
        this.activity = activity;
        OpenCVLoader.initDebug();

        cameraProviderFuture = ProcessCameraProvider.getInstance(this.activity);
    }

    public EyeMoveDetector attachObserverToEyeMoveDetector(Observer observer) {
        eyeMoveDetector.attach(observer);
        return eyeMoveDetector;
    }

    public EyeBlinkDetector attachObserverToBlinkDetector(Observer observer) {
        blinkDetector.attach(observer);
        return blinkDetector;
    }

    public void start() {
        cameraProviderFuture.addListener(prepareCameraProviderRunnable(), ContextCompat.getMainExecutor(activity));
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

    private void bindImageAnalysis(ProcessCameraProvider cameraProvider) {
        ImageAnalysis imageAnalysis = prepareImageAnalysis();
        CameraSelector cameraSelector = prepareCameraSelector();

        cameraProvider.bindToLifecycle(activity, cameraSelector, imageAnalysis);
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
            proxyConverter.setFrame(image);
            Mat frameRGB = proxyConverter.rgb();

            Rect face = faceDetector.detect(frameRGB);
            if (face != null) {
                facemarksDetector.detect(frameRGB, face);
                Point[] rightEyeLandmarks = facemarksDetector.getRightEyeFacemarks();
                Point[] leftEyeLandmarks = facemarksDetector.getLeftEyeFacemarks();
                boolean eyesClosed = closedEyeDetector.areClosed(leftEyeLandmarks, rightEyeLandmarks);

                if (!eyesClosed) {
                    Rect[] eyes = eyeDetector.detect(rightEyeLandmarks, false, leftEyeLandmarks, false);
                    Point[] eyesPupil = new Point[2];

                    for (int i = 0; i < 2; i++) {
                        eyesPupil[i] = eyePupilDetector.detect(frameRGB, eyes[i]);
                    }

                    eyeMoveDetector.tickEyesOpen(eyesPupil, eyes);
                } else
                    eyeMoveDetector.tickEyeClosed();

                blinkDetector.checkEyeBlink(eyesClosed);
            } else
                eyeMoveDetector.tickEyeClosed();

            image.close();
        };
    }
}
