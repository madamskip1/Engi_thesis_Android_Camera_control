package org.pw.engithesis.androidcameracontrol;

import android.graphics.Bitmap;
import android.widget.ImageView;

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
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.concurrent.ExecutionException;

public class CameraCore {

    private final AppCompatActivity activity;
    private final ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private final FaceDetector faceDetector;
    private final EyeDetector eyeDetector;
    private final FPSCounter fpsCounter;
    private final EyePupilDetector pupilDetector;
    ImageProxyToMatConverter proxyConverter = new ImageProxyToMatConverter();
    private ImageView imgView = null;
    private FacemarkDetector facemarkLBF;


    public CameraCore(AppCompatActivity context) {
        OpenCVLoader.initDebug();
        activity = context;
        cameraProviderFuture = ProcessCameraProvider.getInstance(activity);
        faceDetector = new FaceDetector();
        fpsCounter = new FPSCounter();
        eyeDetector = new EyeDetector();
        pupilDetector = new EyePupilDetector();


        //   facemarkLBF = new FacemarkDetector();
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

        cameraProvider.bindToLifecycle(activity, cameraSelector, imageAnalysis);
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
            Mat outputMat = mat.clone();
            Rect face = faceDetector.detect(mat);

            Utility.drawRects(outputMat, new Rect[]{face});

            if (face != null) {
                Rect[] eyes = eyeDetector.detect(mat, face);
                Utility.drawRects(outputMat, eyes);

                pupilDetector.detectPupils(mat, eyes);
                Point[] pupils = pupilDetector.pupils;

                for (Point pupil : pupils) {
                    if (pupil != null) {
                        Imgproc.circle(outputMat, pupil, 2, new Scalar(222, 0, 0), 2);
                    }
                }
            }

            if (imgView != null) {
                Bitmap bitmap = proxyConverter.createBitmap(outputMat);
                imgView.setImageBitmap(bitmap);
            }
            image.close();
        };
    }
}
