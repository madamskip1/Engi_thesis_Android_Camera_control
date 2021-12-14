package org.pw.engithesis.androidcameracontrol.tests;

import android.content.Context;
import android.widget.ScrollView;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.EARClosedEyeDetector;
import org.pw.engithesis.androidcameracontrol.EyeAspectRatio;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.ViewsBuilder;
import org.pw.engithesis.androidcameracontrol.detectors.FaceDetector;
import org.pw.engithesis.androidcameracontrol.detectors.FacemarksDetector;
import org.pw.engithesis.androidcameracontrol.detectors.facemarksdetectionalgorithms.FacemarksKazemi;

import java.util.Locale;

public class EARImageTest extends ImageTest {

    private static final boolean SHOW_IMAGES = true;

    private final FaceDetector faceDetector;
    private final FacemarksDetector facemarksDetector;
    private final EyeAspectRatio eyeAspectRatio = new EyeAspectRatio();

    private int eyesOpenNum = 0;
    private int eyesClosedNum = 0;
    private int correct = 0;
    private int wrong = 0;
    private int correctOpen = 0;
    private int correctClosed = 0;

    public EARImageTest(Context ctx, ScrollView parent) {
        super(ctx, parent);
        faceDetector = new FaceDetector();
        facemarksDetector = new FacemarksDetector(new FacemarksKazemi());
    }

    @Override
    public void createView() {
        ViewsBuilder viewsBuilder = new ViewsBuilder(ctx, parentView);

        for (EARImageTestStruct image : imagesToTest) {
            viewsBuilder.newSection();

            Mat imageMat = getImageMat(image.imgID);
            Rect face = faceDetector.detect(imageMat);
            facemarksDetector.detect(imageMat, face);

            double EARLeftEye = eyeAspectRatio.calcEAR(facemarksDetector.getLeftEyeFacemarks());
            double EARRightEye = eyeAspectRatio.calcEAR(facemarksDetector.getRightEyeFacemarks());
            double[] ear = new double[]{EARRightEye, EARLeftEye};

            boolean leftEyeOpen = EARLeftEye > EARClosedEyeDetector.CLOSED_THRESHOLD;
            boolean rightEyeOpen = EARRightEye > EARClosedEyeDetector.CLOSED_THRESHOLD;
            boolean[] detectedEyesOpen = new boolean[]{rightEyeOpen, leftEyeOpen};

            if (SHOW_IMAGES) {
                viewsBuilder.newSection();
                facemarksDetector.drawFacemarks(imageMat);
                addImageToView(imageMat, viewsBuilder);
                printSingleImgEarStats(ear, detectedEyesOpen, image.eyeOpen, viewsBuilder);
                viewsBuilder.closeSection();
            }

            earStats(detectedEyesOpen, image.eyeOpen);
        }

        viewsBuilder.newSection();
        viewsBuilder.addText("____________________");
        viewsBuilder.addText("____________________");
        viewsBuilder.addText("Przetestowanych zdjęć: " + imagesToTest.length);
        viewsBuilder.addText("Oczy otwarte: " + eyesOpenNum);
        viewsBuilder.addText("Oczy zamknięte: " + eyesClosedNum);
        viewsBuilder.addText("Prawidłowe detekcje: " + correct);
        viewsBuilder.addText("Złe detekcje: " + wrong);
        viewsBuilder.addText("Prawidłowe oczy otwarte: " + correctOpen);
        viewsBuilder.addText("Prawidłowe oczy zamknięte: " + correctClosed);
        viewsBuilder.addText("Złe oczy otwarte: " + (eyesOpenNum - correctOpen));
        viewsBuilder.addText("Złe oczy zamknięte: " + (eyesClosedNum - correctClosed));
        viewsBuilder.addText("____________________");
        viewsBuilder.closeSection();

        viewsBuilder.build();
    }

    private void earStats(boolean[] detected, boolean[] expected) {
        for (int i = 0; i < 2; i++) {
            if (expected[i]) {
                eyesOpenNum++;
            } else {
                eyesClosedNum++;
            }
            if (detected[i] == expected[i]) {
                correct++;
                if (detected[i]) {
                    correctOpen++;
                } else {
                    correctClosed++;
                }
            } else {
                wrong++;
            }
        }
    }

    private void printSingleImgEarStats(double[] ear, boolean[] detected, boolean[] expected, ViewsBuilder viewsBuilder) {
        viewsBuilder.addText("Prawe oko: " + prepareSingleEARStatMsg(ear[0], detected[0], expected[0]));
        viewsBuilder.addText("Lewe oko: " + prepareSingleEARStatMsg(ear[1], detected[1], expected[1]));
    }

    private String prepareSingleEARStatMsg(double ear, boolean detected, boolean expected) {
        String str = String.format(new Locale("pl", "PL"), "%.3f", ear);
        if (detected == expected) {
            str += " (dobrze)";
        } else if (detected) {
            str += " (źle - powinno być zamknięte)";
        } else {
            str += " (źle - powinno być otwarte)";
        }
        return str;
    }

    private static class EARImageTestStruct {
        public int imgID;
        public boolean[] eyeOpen;

        public EARImageTestStruct(int id, boolean[] open) {
            imgID = id;
            eyeOpen = open;
        }
    }

    private final EARImageTestStruct[] imagesToTest = {
            new EARImageTestStruct(R.drawable.face_test_500x500_1, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_2, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_3, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_5, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_6, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_7, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_8, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_9, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_10, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_11, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_12, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_13, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_14, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_15, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_16, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_17, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_18, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_19, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_20, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_21, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_22, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_23, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_24, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_25, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_26, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_27, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_28, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_29, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_30, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_31, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_32, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_33, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_34, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_35, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_36, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_37, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_38, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_39, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_40, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_41, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_42, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_43, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_44, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_45, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_46, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_47, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_48, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_49, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_50, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_51, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_52, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_53, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_55, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_56, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_58, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_59, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_60, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_61, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_62, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_63, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_64, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_65, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_66, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_67, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_68, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_69, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_70, new boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_71, new boolean[]{false, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_72, new boolean[]{true, false}),
            new EARImageTestStruct(R.drawable.face_test_500x500_74, new boolean[]{false, false}),
            new EARImageTestStruct(R.drawable.face_test_500x500_75, new boolean[]{false, false}),
            new EARImageTestStruct(R.drawable.face_test_500x500_76, new boolean[]{false, false}),
            new EARImageTestStruct(R.drawable.face_test_500x500_77, new boolean[]{false, false}),
            new EARImageTestStruct(R.drawable.face_test_500x500_78, new boolean[]{false, true}),
            new EARImageTestStruct(R.drawable.face_test_500x500_79, new boolean[]{true, false}),
            new EARImageTestStruct(R.drawable.face_test_500x500_80, new boolean[]{false, true}),
    };
}
