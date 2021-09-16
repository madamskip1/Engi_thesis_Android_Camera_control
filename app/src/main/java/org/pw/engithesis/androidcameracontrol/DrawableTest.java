package org.pw.engithesis.androidcameracontrol;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.pw.engithesis.androidcameracontrol.facedetectors.HaarCascadeFaceDetector;

public class DrawableTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OpenCVLoader.initDebug();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable_test);

        ImageViewsBuilder imageViewsBuilder = new ImageViewsBuilder(this, findViewById(R.id.activity_drawable));
        DrawableResourceManager drawableResourceManager = new DrawableResourceManager(R.drawable.lenna);

        Mat testMat = drawableResourceManager.getRGBMat();

        HaarCascadeFaceDetector haarCascadeFaceDetector = new HaarCascadeFaceDetector();
        MatOfRect faces = haarCascadeFaceDetector.detect(testMat);
        haarCascadeFaceDetector.drawFaceSquare(testMat, faces);

        Bitmap bm = Bitmap.createBitmap(testMat.cols(), testMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(testMat, bm);

        imageViewsBuilder.addImage(bm);
        imageViewsBuilder.addImage(bm);
        imageViewsBuilder.addImage(bm);
        imageViewsBuilder.addImage(bm);
        imageViewsBuilder.build();
    }
}