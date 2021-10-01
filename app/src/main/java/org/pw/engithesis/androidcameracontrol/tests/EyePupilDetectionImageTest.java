package org.pw.engithesis.androidcameracontrol.tests;

import android.content.Context;
import android.widget.ScrollView;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.pw.engithesis.androidcameracontrol.EyeDetector;
import org.pw.engithesis.androidcameracontrol.EyePupilDetector;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.Utility;
import org.pw.engithesis.androidcameracontrol.ViewsBuilder;
import org.pw.engithesis.androidcameracontrol.facedetectors.HaarCascadeFaceDetector;


public class EyePupilDetectionImageTest extends ImageTest {
    private class EyePupilImageTestStruct {
        public int imgID;
        public Point[] eyes;

        public EyePupilImageTestStruct(int id, Point[] eyesArray) {
            imgID = id;
            eyes = eyesArray;
        }
    }

    private final EyePupilImageTestStruct[] imagesToTest = {
            new EyePupilImageTestStruct(R.drawable.portrait_test_1, new Point[]{new Point(361, 222), new Point(523, 214)}),
            new EyePupilImageTestStruct(R.drawable.portrait_test_2, new Point[]{new Point(217, 410), new Point(373, 416)}),
            new EyePupilImageTestStruct(R.drawable.portrait_test_3, new Point[]{new Point(272, 135), new Point(319, 136)}),
            new EyePupilImageTestStruct(R.drawable.portrait_test_4, new Point[]{new Point(206, 117), new Point(249, 112)}),
            new EyePupilImageTestStruct(R.drawable.portrait_test_5, new Point[]{new Point(255, 175), new Point(362, 182)}),
            new EyePupilImageTestStruct(R.drawable.portrait_test_6, new Point[]{new Point(207, 262), new Point(367, 257)}),
            new EyePupilImageTestStruct(R.drawable.portrait_test_7, new Point[]{new Point(278, 198), new Point(338, 201)}),
            new EyePupilImageTestStruct(R.drawable.portrait_test_8, new Point[]{null, new Point(906, 354)}),
            new EyePupilImageTestStruct(R.drawable.portrait_test_9, new Point[]{new Point(276, 395), null}),
    };

    private final HaarCascadeFaceDetector faceDetector;
    private final EyeDetector eyeDetector;
    private final EyePupilDetector pupilDetector;

    public EyePupilDetectionImageTest(Context ctx, ScrollView parent) {
        super(ctx, parent);
        faceDetector = new HaarCascadeFaceDetector();
        eyeDetector = new EyeDetector();
        pupilDetector = new EyePupilDetector();
    }

    @Override
    public void createView() {
        ViewsBuilder viewsBuilder = new ViewsBuilder(ctx, parentView);

        for (int image = 0; image < imagesToTest.length; image++) {
            viewsBuilder.newSection();

            EyePupilImageTestStruct imageTestStruct = imagesToTest[image];

            Mat imageMat = getImageMat(imageTestStruct.imgID);
            Mat outputMat = imageMat.clone();

            Rect[] faces = getFaceRect(imageMat);
            Rect[] detectedEyes = getEyesRect(imageMat, faces[0]);

            Point[] expectedPupils = imageTestStruct.eyes;
            Point[] detectedPupils = new Point[detectedEyes.length];

            for (int i = 0; i < expectedPupils.length; i++) {
                if (expectedPupils[i] != null) {
                    Imgproc.circle(outputMat, expectedPupils[i], 2, new Scalar(255, 0, 0), 2);
                }
            }

            if (faces.length >= 1) {
                pupilDetector.detectPupils(imageMat, detectedEyes);
                detectedPupils = pupilDetector.pupils;

                for (int i = 0; i < detectedPupils.length; i++) {
                    Imgproc.circle(outputMat, detectedPupils[i], 2, new Scalar(0, 255, 0), 2);
                }
            }

            addImageToView(outputMat, viewsBuilder);

            eyeDetectionStats(expectedPupils, detectedPupils, viewsBuilder);
            viewsBuilder.closeSection();
        }

        viewsBuilder.build();
    }

    private Rect[] getFaceRect(Mat mat) {
        MatOfRect faces = faceDetector.detect(mat);
        return faces.toArray();
    }

    private Rect[] getEyesRect(Mat mat, Rect face) {
        return eyeDetector.detect(mat, face);
    }

    private void eyeDetectionStats(Point[] expectedPupils, Point[] detectedPupils, ViewsBuilder viewsBuilder) {
        for (int i = 0; i < expectedPupils.length; i++) {
            String str = "";
            str += (i == EyeDetector.RIGHT_EYE_INDEX ? "Right eye: " : "Left eye: ");

            if (expectedPupils[i] != null) {
                if (detectedPupils[i] != null) {
                    double distance = Utility.calcDistance(expectedPupils[i], detectedPupils[i]);
                    str += String.format("%.2f", distance) + " px ";
                } else {
                    str += "not detected...";
                }
            } else if (detectedPupils[i] != null) {
                str = "Detected sth else... not eye";
            }

            viewsBuilder.addText(str);
        }
    }


}
