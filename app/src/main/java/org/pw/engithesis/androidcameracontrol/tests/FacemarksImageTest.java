package org.pw.engithesis.androidcameracontrol.tests;

import android.content.Context;
import android.widget.ScrollView;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.ViewsBuilder;
import org.pw.engithesis.androidcameracontrol.detectors.FaceDetector;
import org.pw.engithesis.androidcameracontrol.detectors.FacemarksDetector;

public class FacemarksImageTest extends ImageTest {

    private static final double REPEAT_TEST = 1.0;
    private static final boolean SHOW_IMAGES = true; // show images in ui. Works only if REPEAT_TEST == 1

    private final FaceDetector faceDetector = new FaceDetector();
    private final FacemarksDetector facemarksDetector = new FacemarksDetector();

    protected FacemarksImageTest(Context context, ScrollView parent) {
        super(context, parent);
    }

    @Override
    public void createView() {
        ViewsBuilder viewsBuilder = new ViewsBuilder(ctx, parentView);

        long sumTime = 0;
        long start = System.nanoTime();

        for (int i = 0; i < REPEAT_TEST; i++) {
            for (int imageToTest : imagesToTest) {
                Mat imageMat = getImageMat(imageToTest);
                Rect face = faceDetector.detect(imageMat);

                long singleStart = System.nanoTime();
                facemarksDetector.detect(imageMat, face);
                sumTime += System.nanoTime() - singleStart;

                if (canShowImage()) {
                    viewsBuilder.newSection();
                    facemarksDetector.drawFacemarks(imageMat);
                    addImageToView(imageMat, viewsBuilder);
                    viewsBuilder.closeSection();
                }
            }
        }

        long end = System.nanoTime();
        double timeInSec = (end - start) / (double) 1_000_000_000;
        double sumTimeInSec = sumTime / (double) 1_000_000_000;

        viewsBuilder.newSection();
        viewsBuilder.addText("____________________");
        viewsBuilder.addText("____________________");
        viewsBuilder.addText("Tested images: " + imagesToTest.length);
        viewsBuilder.addText("Total time: " + timeInSec + " s");
        viewsBuilder.addText("Avg test time: " + (timeInSec / REPEAT_TEST) + " s");
        viewsBuilder.addText("Detections total time: " + sumTimeInSec + " s");
        viewsBuilder.addText("Avg detections time: " + (sumTimeInSec / REPEAT_TEST) + " s");
        viewsBuilder.addText("One detection avg time: " + (sumTimeInSec / (double) imagesToTest.length / REPEAT_TEST) + " s");
        viewsBuilder.addText("____________________");
        viewsBuilder.closeSection();

        viewsBuilder.build();
    }

    private boolean canShowImage() {
        return SHOW_IMAGES && REPEAT_TEST == 1.0;
    }


    private final int[] imagesToTest = {
            R.drawable.face_test_500x500_1,
            R.drawable.face_test_500x500_2,
            R.drawable.face_test_500x500_3,
            R.drawable.face_test_500x500_4,
            R.drawable.face_test_500x500_5,
            R.drawable.face_test_500x500_6,
            R.drawable.face_test_500x500_7,
            R.drawable.face_test_500x500_8,
            R.drawable.face_test_500x500_9,
            R.drawable.face_test_500x500_10,
            R.drawable.face_test_500x500_11,
            R.drawable.face_test_500x500_12,
            R.drawable.face_test_500x500_13,
            R.drawable.face_test_500x500_14,
            R.drawable.face_test_500x500_15,
            R.drawable.face_test_500x500_16,
            R.drawable.face_test_500x500_17,
            R.drawable.face_test_500x500_18,
            R.drawable.face_test_500x500_19,
            R.drawable.face_test_500x500_20,
            R.drawable.face_test_500x500_21,
            R.drawable.face_test_500x500_22,
            R.drawable.face_test_500x500_23,
            R.drawable.face_test_500x500_24,
            R.drawable.face_test_500x500_25,
            R.drawable.face_test_500x500_26,
            R.drawable.face_test_500x500_27,
            R.drawable.face_test_500x500_28,
            R.drawable.face_test_500x500_29,
            R.drawable.face_test_500x500_30,
            R.drawable.face_test_500x500_31,
            R.drawable.face_test_500x500_32,
            R.drawable.face_test_500x500_33,
            R.drawable.face_test_500x500_34,
            R.drawable.face_test_500x500_35,
            R.drawable.face_test_500x500_36,
            R.drawable.face_test_500x500_37,
            R.drawable.face_test_500x500_38,
            R.drawable.face_test_500x500_39,
            R.drawable.face_test_500x500_40,
            R.drawable.face_test_500x500_41,
            R.drawable.face_test_500x500_42,
            R.drawable.face_test_500x500_43,
            R.drawable.face_test_500x500_44,
            R.drawable.face_test_500x500_45,
            R.drawable.face_test_500x500_46,
            R.drawable.face_test_500x500_47,
            R.drawable.face_test_500x500_48,
            R.drawable.face_test_500x500_49,
            R.drawable.face_test_500x500_50,
            R.drawable.face_test_500x500_51,
            R.drawable.face_test_500x500_52,
            R.drawable.face_test_500x500_53,
            R.drawable.face_test_500x500_55,
            R.drawable.face_test_500x500_56,
            R.drawable.face_test_500x500_58,
            R.drawable.face_test_500x500_59,
            R.drawable.face_test_500x500_60,
            R.drawable.face_test_500x500_61,
            R.drawable.face_test_500x500_62,
            R.drawable.face_test_500x500_63,
            R.drawable.face_test_500x500_64,
            R.drawable.face_test_500x500_65,
            R.drawable.face_test_500x500_66,
            R.drawable.face_test_500x500_67,
            R.drawable.face_test_500x500_68,
            R.drawable.face_test_500x500_69,
            R.drawable.face_test_500x500_70,
            R.drawable.face_test_500x500_71,
            R.drawable.face_test_500x500_72,
            R.drawable.face_test_500x500_73,
            R.drawable.face_test_500x500_74,
            R.drawable.face_test_500x500_75,
            R.drawable.face_test_500x500_76,
            R.drawable.face_test_500x500_77,
            R.drawable.face_test_500x500_78,
            R.drawable.face_test_500x500_79,
            R.drawable.face_test_500x500_80,
    };
}
