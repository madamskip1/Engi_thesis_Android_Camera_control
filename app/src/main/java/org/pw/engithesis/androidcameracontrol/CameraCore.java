package org.pw.engithesis.androidcameracontrol;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
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
import org.pw.engithesis.androidcameracontrol.facedetectors.HaarCascadeFaceDetector;
import org.pw.engithesis.androidcameracontrol.interfaces.FPSView;
import org.pw.engithesis.androidcameracontrol.interfaces.ResourceManager;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CameraCore {

    private static final String TAG = "CameraCore";
    private AppCompatActivity activity;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    ImageProxyToMatConverter proxyConverter = new ImageProxyToMatConverter();
    private ImageView imgView = null;
    private FaceDetector faceDetector;
    private FPSView fpsView = null;
    private Facemark facemark;

    public CameraCore(AppCompatActivity context) {
        OpenCVLoader.initDebug();
        activity = context;
        cameraProviderFuture = ProcessCameraProvider.getInstance(activity);
        faceDetector = new HaarCascadeFaceDetector();
        facemark = Face.createFacemarkLBF();
        ResourceManager resourceManager = new ResourceManager(R.raw.lbfmodel, "lbfmodel.yaml");
        facemark.loadModel(resourceManager.getResourcePath());
    }

    /*
        Finalnie nie bedzie wyswietlania - wtedy usunac trzeba
        Wyswietlanie tylko do testow
     */
    public void setImageView(ImageView imageView) {
        imgView = imageView;
    }

    public void setFpsView(FPSView view) {
        fpsView = view;
    }

    public void start() {
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
        }, ContextCompat.getMainExecutor(activity));
    }

    private void bindImageAnalysis(ProcessCameraProvider cameraProvider) {
        @SuppressLint("RestrictedApi") ImageAnalysis imageAnalyzer = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalyzer.setAnalyzer(ContextCompat.getMainExecutor(activity), new ImageAnalysis.Analyzer() {
            private Long firstTimestamp = Long.valueOf(-1);
            private Long previousTimestamp = System.currentTimeMillis();//new Long(-1);
            private double frameCounter = 0.0;

            @Override
            public void analyze(@NonNull ImageProxy image) {

                if (fpsView != null) {
                    calcFPS();
                }

                proxyConverter.setFrame(image);
                Mat mat = proxyConverter.rgb();
                MatOfRect faces = faceDetector.detect(mat);

                ArrayList<MatOfPoint2f> landmarks = new ArrayList<MatOfPoint2f>();
                facemark.fit(mat, faces, landmarks);

                for (int i=0; i<landmarks.size(); i++) {
                    MatOfPoint2f lm = landmarks.get(i);
                    for (int j=0; j<lm.rows(); j++) {
                        double [] dp = lm.get(j,0);
                        Point p = new Point(dp[0], dp[1]);
                        Imgproc.circle(mat,p,2,new Scalar(222),1);
                    }
                }


                faceDetector.drawFaceSquare(mat, faces);


                if (imgView != null) {
                    Bitmap bitmap = proxyConverter.createBitmap(mat);
                    imgView.setImageBitmap(bitmap);
                }
                image.close();
            }



            private void calcFPS() {
                frameCounter++;
                Long currentTime = System.currentTimeMillis();

                if (firstTimestamp == -1) {
                    firstTimestamp = currentTime;
                }

                double curFPS = 1.0 / (currentTime - previousTimestamp) * 1000.0;
                double avgFPS = frameCounter / (currentTime - firstTimestamp) * 1000.0;

                fpsView.setFPSText(curFPS, avgFPS);
                previousTimestamp = currentTime;
            }
        });


        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        cameraProvider.bindToLifecycle((LifecycleOwner) activity, cameraSelector, imageAnalyzer);
    }
}
