package org.pw.engithesis.androidcameracontrol.tests;

import android.content.Context;
import android.widget.ScrollView;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.Utility;
import org.pw.engithesis.androidcameracontrol.ViewsBuilder;
import org.pw.engithesis.androidcameracontrol.detectors.EyePupilDetector;

import java.util.Locale;


public class EyePupilDetectorImageTest extends ImageTest {
    private static final boolean SHOW_IMAGES = true;
    private static final double REPEAT_TEST = 1.0;

    private final EyePupilDetector pupilDetector = new EyePupilDetector();
    private double errorSum = 0.0;
    private int outsideIris = 0;
    private int insideIris = 0;
    private int in10Good = 0;
    private int in5Good = 0;
    private int in1Good = 0;

    public EyePupilDetectorImageTest(Context ctx, ScrollView parent) {
        super(ctx, parent);
    }

    @Override
    public void createView() {
        ViewsBuilder viewsBuilder = new ViewsBuilder(ctx, parentView);

        long detectionTotalTime = 0;
        long startTestTime = System.nanoTime();

        for (int i = 0; i < REPEAT_TEST; i++) {
            for (EyePupilImageTestStruct image : imagesToTest) {
                Mat imageMat = getImageMat(image.imgID);
                Rect eyeRect = new Rect(0, 0, imageMat.width(), imageMat.height());

                Point expectedPupilCenter = image.pupilCenter;
                Rect expectedIrisRect = image.irisRect;

                long startDetectionTime = System.nanoTime();
                Point detectedPupilCenter = pupilDetector.detect(imageMat, eyeRect);
                detectionTotalTime += System.nanoTime() - startDetectionTime;

                if (canShowImage()) {
                    viewsBuilder.newSection();
                    Utility.drawRect(imageMat, expectedIrisRect, new Scalar(0, 0, 255), 1);
                    Utility.drawCircle(imageMat, detectedPupilCenter, new Scalar(0, 255, 0), 1, 1);
                    Utility.drawCircle(imageMat, expectedPupilCenter, new Scalar(60, 130, 255), 1, 1);
                    addImageToView(imageMat, viewsBuilder);
                    eyeDetectionStats(expectedPupilCenter, expectedIrisRect, detectedPupilCenter, imageMat.width(), imageMat.height(), viewsBuilder);
                    viewsBuilder.closeSection();
                } else {
                    eyeDetectionStats(expectedPupilCenter, expectedIrisRect, detectedPupilCenter, imageMat.width(), imageMat.height(), viewsBuilder);
                }
            }
        }

        long endTestTime = System.nanoTime();
        double testTotalTimeInSec = (endTestTime - startTestTime) / (double) 1_000_000_000;
        double detectionTotalTimeInSec = detectionTotalTime / (double) 1_000_000_000;

        viewsBuilder.newSection();
        viewsBuilder.addText("____________________");
        viewsBuilder.addText("____________________");
        viewsBuilder.addText("Przetestowanych zdjęć oczu: " + imagesToTest.length);
        viewsBuilder.addText("W obszare tęczówki: " + (int) (insideIris / REPEAT_TEST));
        viewsBuilder.addText("Poza obszarem tęczówki: " + (int) (outsideIris / REPEAT_TEST));
        viewsBuilder.addText("Śr. błąd: " + String.format(new Locale("pl", "PL"), "%.2f", (errorSum / (double) imagesToTest.length / REPEAT_TEST * 100)) + "%");
        viewsBuilder.addText("Błąd <= 0.10: " + (int) (in10Good / REPEAT_TEST));
        viewsBuilder.addText("Błąd <= 0.05: " + (int) (in5Good / REPEAT_TEST));
        viewsBuilder.addText("Błąd <= 0.01: " + (int) (in1Good / REPEAT_TEST));
        viewsBuilder.addText("____________________");
        viewsBuilder.addText("Całkowity czas: " + testTotalTimeInSec + " s");
        viewsBuilder.addText("Śr. czas testu: " + testTotalTimeInSec / REPEAT_TEST + " s");
        viewsBuilder.addText("Całkowity czas detekcji: " + detectionTotalTimeInSec + " s");
        viewsBuilder.addText("Śr. czas detekcji testu: " + (detectionTotalTimeInSec / REPEAT_TEST) + " s");
        viewsBuilder.addText("Śr. czas pojedynczej detekcji: " + (detectionTotalTimeInSec / (double) imagesToTest.length / REPEAT_TEST) + " s");
        viewsBuilder.addText("____________________");
        viewsBuilder.addText("____________________");
        viewsBuilder.closeSection();
        viewsBuilder.build();
    }

    private void eyeDetectionStats(Point expectedPupilCenter, Rect expectedIrisRect, Point detectedPupilCenter, int width, int height, ViewsBuilder viewsBuilder) {
        boolean isInsideIris = detectedPupilCenter.inside(expectedIrisRect);
        if (isInsideIris) {
            insideIris++;
        } else {
            outsideIris++;
        }

        double distance = Utility.calcDistance(expectedPupilCenter, detectedPupilCenter);
        double error = distance / Math.hypot(width, height);
        errorSum += error;

        if (error <= 0.1) {
            in10Good++;
            if (error <= 0.05) {
                in5Good++;
                if (error <= 0.01) {
                    in1Good++;
                }
            }
        }

        if (canShowImage()) {
            viewsBuilder.addText("W środku tęczówki: " + (isInsideIris ? "Tak" : "Nie"));
            viewsBuilder.addText("Błąd: " + String.format(new Locale("pl", "PL"), "%.2f", error * 100) + "%");
        }
    }

    private boolean canShowImage() {
        return SHOW_IMAGES && REPEAT_TEST == 1.0;
    }


    private static class EyePupilImageTestStruct {
        public final int imgID;
        public final Point pupilCenter;
        public final Rect irisRect;

        public EyePupilImageTestStruct(int imgId, Point pupilCenter, Rect irisRect) {
            this.imgID = imgId;
            this.pupilCenter = pupilCenter;
            this.irisRect = irisRect;
        }
    }

    private final EyePupilImageTestStruct[] imagesToTest = {
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_0, new Point(27, 13), new Rect(17, 6, 20, 17)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_1, new Point(32, 14), new Rect(22, 5, 21, 18)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_2, new Point(23, 14), new Rect(15, 6, 17, 17)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_3, new Point(27, 15), new Rect(18, 6, 17, 16)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_4, new Point(23, 13), new Rect(15, 6, 16, 15)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_5, new Point(23, 13), new Rect(15, 5, 17, 16)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_7, new Point(19, 10), new Rect(10, 4, 17, 14)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_8, new Point(30, 15), new Rect(17, 5, 24, 21)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_9, new Point(30, 15), new Rect(19, 5, 24, 20)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_10, new Point(11, 4), new Rect(6, 2, 9, 5)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_11, new Point(9, 4), new Rect(6, 1, 9, 5)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_12, new Point(25, 18), new Rect(17, 11, 16, 15)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_13, new Point(29, 18), new Rect(20, 11, 17, 15)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_14, new Point(36, 23), new Rect(21, 13, 28, 22)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_15, new Point(41, 24), new Rect(28, 15, 27, 20)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_16, new Point(25, 20), new Rect(15, 13, 20, 16)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_17, new Point(25, 20), new Rect(15, 12, 18, 17)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_18, new Point(14, 23), new Rect(7, 17, 13, 13)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_19, new Point(13, 12), new Rect(7, 6, 12, 13)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_20, new Point(24, 11), new Rect(14, 3, 21, 17)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_21, new Point(31, 12), new Rect(22, 4, 20, 17)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_22, new Point(31, 11), new Rect(19, 4, 23, 16)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_23, new Point(33, 11), new Rect(20, 1, 26, 20)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_24, new Point(23, 12), new Rect(14, 3, 19, 18)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_25, new Point(17, 13), new Rect(9, 6, 18, 16)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_26, new Point(14, 12), new Rect(8, 6, 12, 12)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_27, new Point(13, 10), new Rect(8, 5, 10, 11)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_28, new Point(21, 12), new Rect(13, 4, 16, 15)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_28, new Point(21, 12), new Rect(13, 4, 15, 16)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_29, new Point(24, 13), new Rect(17, 6, 14, 15)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_30, new Point(36, 17), new Rect(25, 7, 22, 20)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_31, new Point(34, 17), new Rect(23, 6, 22, 21)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_32, new Point(38, 25), new Rect(25, 13, 27, 25)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_33, new Point(40, 24), new Rect(27, 12, 26, 26)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_34, new Point(33, 19), new Rect(22, 9, 22, 20)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_35, new Point(39, 19), new Rect(26, 8, 25, 22)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_36, new Point(14, 5), new Rect(8, 1, 11, 9)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_37, new Point(12, 7), new Rect(7, 3, 11, 9)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_38, new Point(28, 15), new Rect(19, 6, 19, 19)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_39, new Point(24, 18), new Rect(15, 9, 18, 18)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_40, new Point(33, 15), new Rect(20, 5, 27, 20)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_41, new Point(36, 15), new Rect(23, 6, 25, 18)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_42, new Point(32, 14), new Rect(20, 5, 27, 21)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_43, new Point(38, 11), new Rect(25, 3, 26, 19)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_44, new Point(22, 10), new Rect(14, 4, 14, 11)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_45, new Point(22, 8), new Rect(14, 3, 16, 10)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_46, new Point(16, 11), new Rect(10, 6, 12, 11)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_47, new Point(17, 11), new Rect(11, 5, 11, 12)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_48, new Point(32, 14), new Rect(21, 7, 22, 16)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_49, new Point(32, 13), new Rect(20, 6, 24, 16)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_50, new Point(28, 14), new Rect(16, 5, 23, 18)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_51, new Point(30, 13), new Rect(19, 4, 22, 19)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_52, new Point(32, 13), new Rect(20, 4, 22, 20)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_53, new Point(35, 11), new Rect(23, 2, 23, 21)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_54, new Point(32, 16), new Rect(17, 6, 27, 21)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_55, new Point(32, 16), new Rect(18, 6, 27, 21)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_56, new Point(18, 11), new Rect(12, 5, 12, 13)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_57, new Point(18, 11), new Rect(10, 5, 14, 14)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_58, new Point(14, 6), new Rect(8, 2, 11, 10)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_59, new Point(14, 8), new Rect(9, 3, 10, 10)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_60, new Point(15, 14), new Rect(9, 7, 13, 14)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_61, new Point(25, 12), new Rect(17, 5, 17, 15)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_62, new Point(41, 21), new Rect(31, 12, 24, 19)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_63, new Point(40, 22), new Rect(29, 12, 23, 19)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_64, new Point(27, 8), new Rect(19, 3, 17, 12)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_65, new Point(30, 7), new Rect(20, 2, 20, 11)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_66, new Point(37, 23), new Rect(25, 14, 23, 19)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_67, new Point(32, 31), new Rect(21, 21, 23, 20)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_68, new Point(18, 6), new Rect(12, 5, 15, 9)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_69, new Point(21, 6), new Rect(15, 4, 15, 10)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_70, new Point(34, 18), new Rect(23, 10, 22, 19)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_71, new Point(33, 18), new Rect(23, 11, 21, 18)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_72, new Point(30, 9), new Rect(17, 2, 26, 15)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_73, new Point(35, 12), new Rect(21, 4, 24, 17)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_74, new Point(16, 9), new Rect(11, 5, 11, 9)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_75, new Point(20, 8), new Rect(14, 4, 14, 10)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_76, new Point(17, 10), new Rect(10, 5, 14, 13)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_77, new Point(19, 11), new Rect(12, 5, 12, 13)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_78, new Point(37, 16), new Rect(20, 7, 31, 21)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_79, new Point(37, 15), new Rect(24, 7, 28, 21)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_80, new Point(37, 18), new Rect(26, 10, 21, 19)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_81, new Point(30, 19), new Rect(20, 12, 20, 18)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_82, new Point(32, 15), new Rect(20, 5, 23, 16)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_83, new Point(38, 13), new Rect(26, 5, 24, 14)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_84, new Point(24, 15), new Rect(15, 9, 17, 15)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_85, new Point(27, 10), new Rect(18, 3, 18, 15)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_86, new Point(20, 12), new Rect(12, 7, 15, 13)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_87, new Point(18, 11), new Rect(10, 6, 16, 11)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_88, new Point(30, 16), new Rect(18, 7, 23, 19)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_89, new Point(38, 16), new Rect(27, 7, 23, 20)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_90, new Point(35, 10), new Rect(23, 3, 23, 15)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_91, new Point(29, 14), new Rect(20, 7, 18, 17)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_92, new Point(29, 15), new Rect(20, 9, 18, 16)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_93, new Point(28, 15), new Rect(17, 8, 22, 17)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_94, new Point(33, 12), new Rect(21, 4, 22, 18)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_95, new Point(40, 27), new Rect(25, 16, 28, 23)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_96, new Point(41, 25), new Rect(26, 15, 28, 24)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_97, new Point(12, 5), new Rect(6, 2, 13, 8)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_98, new Point(18, 7), new Rect(12, 4, 11, 8)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_99, new Point(12, 6), new Rect(5, 3, 13, 8)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_100, new Point(14, 6), new Rect(9, 3, 12, 9)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_101, new Point(14, 6), new Rect(9, 2, 11, 9)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_102, new Point(17, 4), new Rect(11, 0, 13, 9)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_103, new Point(9, 5), new Rect(4, 2, 11, 7)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_104, new Point(10, 5), new Rect(6, 2, 9, 7)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_105, new Point(15, 7), new Rect(10, 3, 11, 9)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_106, new Point(16, 7), new Rect(11, 3, 11, 9)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_107, new Point(22, 10), new Rect(15, 6, 10, 11)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_108, new Point(29, 10), new Rect(22, 5, 10, 13)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_109, new Point(25, 13), new Rect(15, 5, 19, 15)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_110, new Point(25, 12), new Rect(15, 5, 19, 16)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_111, new Point(25, 17), new Rect(15, 9, 18, 17)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_112, new Point(25, 17), new Rect(18, 9, 17, 18)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_113, new Point(11, 5), new Rect(6, 1, 11, 8)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_114, new Point(14, 7), new Rect(9, 3, 11, 9)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_115, new Point(13, 8), new Rect(7, 3, 9, 9)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_116, new Point(16, 9), new Rect(11, 4, 10, 9)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_117, new Point(17, 5), new Rect(11, 1, 13, 8)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_118, new Point(38, 14), new Rect(29, 6, 17, 17)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_119, new Point(42, 13), new Rect(34, 6, 15, 15)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_120, new Point(17, 15), new Rect(9, 8, 15, 12)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_121, new Point(15, 14), new Rect(9, 7, 13, 12)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_122, new Point(39, 25), new Rect(23, 13, 29, 20)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_123, new Point(39, 25), new Rect(26, 13, 27, 18)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_124, new Point(17, 11), new Rect(10, 6, 15, 9)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_125, new Point(19, 12), new Rect(13, 7, 13, 10)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_126, new Point(17, 8), new Rect(10, 2, 14, 11)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_127, new Point(19, 10), new Rect(12, 4, 14, 10)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_128, new Point(21, 13), new Rect(13, 7, 15, 13)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_129, new Point(22, 12), new Rect(14, 6, 16, 12)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_130, new Point(18, 7), new Rect(11, 2, 14, 12)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_131, new Point(30, 10), new Rect(18, 2, 23, 18)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_134, new Point(27, 14), new Rect(18, 7, 19, 15)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_138, new Point(27, 13), new Rect(18, 5, 17, 17)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_139, new Point(27, 13), new Rect(17, 7, 19, 14)),
            new EyePupilImageTestStruct(R.drawable.eye_pupil_test_from_500x500_140, new Point(12, 5), new Rect(7, 1, 10, 9)),
    };
}
