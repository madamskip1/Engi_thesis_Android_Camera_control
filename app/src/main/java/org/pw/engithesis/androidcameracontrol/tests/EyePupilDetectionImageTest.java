package org.pw.engithesis.androidcameracontrol.tests;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ScrollView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.pw.engithesis.androidcameracontrol.DrawableResourceManager;
import org.pw.engithesis.androidcameracontrol.EyeDetector;
import org.pw.engithesis.androidcameracontrol.EyePupilDetector;
import org.pw.engithesis.androidcameracontrol.ViewsBuilder;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.facedetectors.HaarCascadeFaceDetector;

public class EyePupilDetectionImageTest {
    private int[] imagesToTest = {
            R.drawable.portrait_test_1,
            R.drawable.portrait_test_2,
            R.drawable.portrait_test_3,
            R.drawable.portrait_test_4,
            R.drawable.portrait_test_5,
            R.drawable.portrait_test_6,
            R.drawable.portrait_test_7,
            R.drawable.portrait_test_8
    };

    private Context ctx;
    private ScrollView parentView;
    private HaarCascadeFaceDetector faceDetector;
    private EyeDetector eyeDetector;
    private EyePupilDetector pupilDetector;

    public EyePupilDetectionImageTest(Context ctx, ScrollView parent) {
        OpenCVLoader.initDebug();
        this.ctx = ctx;
        parentView = parent;
        faceDetector = new HaarCascadeFaceDetector();
        eyeDetector = new EyeDetector();
        pupilDetector = new EyePupilDetector();
    }

    public void createView() {
        ViewsBuilder viewsBuilder = new ViewsBuilder(ctx, parentView);

        for (int imageID = 0; imageID < imagesToTest.length; imageID++) {
            viewsBuilder.newSection();

            Mat imageMat = getImageMat(imagesToTest[imageID]);
            Rect[] faces = getFaceRect(imageMat);

            if (faces.length >= 1) {
                Rect[] eyes = getEyesRect(imageMat, faces[0]);
                for (int eye = 0; eye < eyes.length; eye++) {
                    Point pupil = pupilDetector.detect(imageMat, eyes[eye]);
                    Imgproc.circle(imageMat, pupil, 1, new Scalar(0, 200, 0), 2);
                    viewsBuilder.addText("Pupil. x: " + pupil.x + ", " + pupil.y);

                }
            }

            addImageToView(imageMat, viewsBuilder);
            viewsBuilder.closeSection();
        }

        viewsBuilder.build();
    }

    private void addImageToView(Mat mat, ViewsBuilder viewsBuilder) {
        Bitmap temp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, temp);

        viewsBuilder.addImage(temp);
    }

    private Mat getImageMat(int imageID) {
        DrawableResourceManager drawableResourceManager = new DrawableResourceManager(imageID);
        return drawableResourceManager.getRGBMat();
    }

    private Rect[] getFaceRect(Mat mat) {
        MatOfRect faces = faceDetector.detect(mat);
        return faces.toArray();
    }

    private Rect[] getEyesRect(Mat mat, Rect face) {
        MatOfRect eyes = eyeDetector.detect(mat, face);
        return eyes.toArray();
    }
}
