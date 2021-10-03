package org.pw.engithesis.androidcameracontrol;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.facedetectors.HaarCascadeFaceDetector;

import java.util.ArrayList;

public class DrawableTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OpenCVLoader.initDebug();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable_test);

        ViewsBuilder viewsBuilder = new ViewsBuilder(this, findViewById(R.id.activity_drawable));
        DrawableResourceManager drawableResourceManager = new DrawableResourceManager(R.drawable.test);

        Mat testMat = drawableResourceManager.getRGBMat();

        HaarCascadeFaceDetector haarCascadeFaceDetector = new HaarCascadeFaceDetector();
        Rect[] faces = haarCascadeFaceDetector.detect(testMat);
        Utility.drawRects(testMat, faces);

        FacemarkDetector facemarkDetector = new FacemarkDetector();
        ArrayList<MatOfPoint2f> landamrsks = facemarkDetector.detect(testMat, new MatOfRect(faces));
        facemarkDetector.drawLandmarks(testMat, landamrsks);

        EyeBlinkDetector eyeBlinkDetector = new EyeBlinkDetector();
        eyeBlinkDetector.checkEyeBlink(landamrsks.get(0));

        Log.e("fm", "ear: " + eyeBlinkDetector.leftEyeEAR + ", " + eyeBlinkDetector.rightEyeEAR);

        Bitmap bm = Bitmap.createBitmap(testMat.cols(), testMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(testMat, bm);

        viewsBuilder.addImage(bm);
        viewsBuilder.addImage(bm);
        viewsBuilder.addImage(bm);
        viewsBuilder.addImage(bm);
        viewsBuilder.build();
    }
}