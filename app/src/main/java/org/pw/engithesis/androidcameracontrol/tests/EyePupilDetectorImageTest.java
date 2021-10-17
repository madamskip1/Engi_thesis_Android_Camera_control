package org.pw.engithesis.androidcameracontrol.tests;

import android.content.Context;
import android.widget.ScrollView;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.pw.engithesis.androidcameracontrol.EyeDetector;
import org.pw.engithesis.androidcameracontrol.EyePupilDetector;
import org.pw.engithesis.androidcameracontrol.MatToFile;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.Utility;
import org.pw.engithesis.androidcameracontrol.ViewsBuilder;
import org.pw.engithesis.androidcameracontrol.facedetectors.HaarCascadeFaceDetector;


public class EyePupilDetectorImageTest extends ImageTest {
    private final EyePupilImageTestStruct[] imagesToTest = {
            new EyePupilImageTestStruct(R.drawable.lenna, new Point[]{new Point(265, 265), new Point(328, 265)}),
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
    public EyePupilDetectorImageTest(Context ctx, ScrollView parent) {
        super(ctx, parent);
        faceDetector = new HaarCascadeFaceDetector();
        eyeDetector = new EyeDetector();
        pupilDetector = new EyePupilDetector();
    }

    @Override
    public void createView() {
        ViewsBuilder viewsBuilder = new ViewsBuilder(ctx, parentView);

        MatToFile matToFile = new MatToFile();

        int i = 0;

        for (EyePupilImageTestStruct image : imagesToTest) {
            viewsBuilder.newSection();

            Mat imageMat = getImageMat(image.imgID);
            Mat outputMat = imageMat.clone();

            Rect face = getFaceRect(imageMat);
            Rect[] detectedEyes = getEyesRect(imageMat, face);

            Point[] expectedPupils = image.eyes;
            Point[] detectedPupils = new Point[detectedEyes.length];

            for (Point expectedPupil : expectedPupils) {
                if (expectedPupil != null) {
                    Imgproc.circle(outputMat, expectedPupil, 2, new Scalar(255, 0, 0), 2);
                }
            }

            if (face != null) {
                pupilDetector.detectPupils(imageMat, detectedEyes);
                detectedPupils = pupilDetector.pupils;

                for (Point detectedPupil : detectedPupils) {
                    if (detectedPupil != null) {
                        Imgproc.circle(outputMat, detectedPupil, 2, new Scalar(0, 255, 0), 2);
                    }
                }
            }

            addImageToView(outputMat, viewsBuilder);

            eyeDetectionStats(expectedPupils, detectedPupils, viewsBuilder);
            viewsBuilder.closeSection();

            //matToFile.saveRGBMatAsPNG("pupilDetector" + i, outputMat);
            i++;
        }

        viewsBuilder.build();
    }

    private Rect getFaceRect(Mat mat) {
        return faceDetector.detect(mat);
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

    private static class EyePupilImageTestStruct {
        public int imgID;
        public Point[] eyes;

        public EyePupilImageTestStruct(int id, Point[] eyesArray) {
            imgID = id;
            eyes = eyesArray;
        }
    }


}
