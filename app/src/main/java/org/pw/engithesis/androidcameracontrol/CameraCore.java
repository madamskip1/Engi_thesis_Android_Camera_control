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
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.face.Face;
import org.opencv.face.Facemark;
import org.opencv.imgproc.Imgproc;
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
    private Facemark facemark;
    private FPSCounter fpsCounter;


    public CameraCore(AppCompatActivity context) {
        OpenCVLoader.initDebug();
        activity = context;
        cameraProviderFuture = ProcessCameraProvider.getInstance(activity);
        faceDetector = new LbpCascadeFaceDetector();
        facemark = Face.createFacemarkLBF();
        RawResourceManager rawResourceManager = new RawResourceManager(R.raw.lbfmodel, "lbfmodel.yaml");
        facemark.loadModel(rawResourceManager.getPath());
        fpsCounter = new FPSCounter();

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

            ArrayList<MatOfPoint2f> landmarks = new ArrayList<>();
            facemark.fit(mat, faces, landmarks);

            for (int i = 0; i < landmarks.size(); i++) {
                MatOfPoint2f lm = landmarks.get(i);
                for (int j = 0; j < lm.rows(); j++) {
                    double[] dp = lm.get(j, 0);
                    Point p = new Point(dp[0], dp[1]);
                    Imgproc.circle(mat, p, 2, new Scalar(222), 1);
                }
            }

            faceDetector.drawFaceSquare(mat, faces);


            if (imgView != null) {
                Bitmap bitmap = proxyConverter.createBitmap(mat);
                imgView.setImageBitmap(bitmap);
            }
            image.close();
        };
    }
}
