package org.pw.engithesis.androidcameracontrol.tests;

import android.content.Context;
import android.widget.ScrollView;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.pw.engithesis.androidcameracontrol.EARClosedEyeDetector;
import org.pw.engithesis.androidcameracontrol.EyeAspectRatio;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.Utility;
import org.pw.engithesis.androidcameracontrol.ViewsBuilder;
import org.pw.engithesis.androidcameracontrol.detectors.EyeDetector;
import org.pw.engithesis.androidcameracontrol.detectors.FaceDetector;
import org.pw.engithesis.androidcameracontrol.detectors.FacemarksDetector;
import org.pw.engithesis.androidcameracontrol.detectors.eyedetectionalgorithms.EyeDetectionFacemarks;

import java.util.Locale;

public class EyeDetectorImageTest extends ImageTest {
    private static final boolean DETECT_IN_GRAY = false;
    private static final double REPEAT_TEST = 1.0;
    private static final boolean SHOW_IMAGES = true; // show images in ui. Works only if REPEAT_TEST == 1

    private final FaceDetector faceDetector = new FaceDetector();
    private final EyeDetector eyeDetector = new EyeDetector();
    private final FacemarksDetector facemarksDetector = new FacemarksDetector();
    private final EyeAspectRatio eyeAspectRatio = new EyeAspectRatio();
    private final EyeDetectionFacemarks eyeDetectionFacemarks = new EyeDetectionFacemarks();

    private int numCorrect = 0;
    private int numPerfect = 0;
    private int numPartialCorrect = 0;
    private int numThreeSideCorrect = 0;
    private int numWrong = 0;
    private int closedWrong = 0;
    private int openWrong = 0;
    private int closedCorrect = 0;
    private int totalOpenEyes = 0;
    private int totalClosedEyes = 0;
    private double wrongSum = 0;
    private int wrongSideCounter = 0;


    protected EyeDetectorImageTest(Context context, ScrollView parent) {
        super(context, parent);
    }

    @Override
    public void createView() {
        ViewsBuilder viewsBuilder = new ViewsBuilder(ctx, parentView);


        long sumTime = 0;
        long start = System.nanoTime();

        for (int j = 0; j < REPEAT_TEST; j++) {
            for (EyesImageTestStruct imageToTest : imagesToTest) {
                Mat imageMat = getImageMat(imageToTest.imgID);
                Rect face = faceDetector.detect(imageMat);
                Rect[] eyes;

                /*      HAAR    */
                /*
               if (!DETECT_IN_GRAY) {
                    long singleStart = System.nanoTime();
                    eyes = eyeDetector.detect(imageMat, face);
                    timeSum += System.nanoTime() - singleStart;
                }
                else
                {
                    Mat grayImage = new Mat();
                    Imgproc.cvtColor(imageMat, grayImage, Imgproc.COLOR_RGB2GRAY);
                    long singleStart = System.nanoTime();
                    eyes = eyeDetector.detect(grayImage, face);
                    timeSum += System.nanoTime() - singleStart;
                }
                */
                /* ************ */


                /*      Facemark    */
                long singleStart = System.nanoTime();
                facemarksDetector.detect(imageMat, face);
                Point[] rightEyeLandmarks = facemarksDetector.getRightEyeFacemarks();
                Point[] leftEyeLandmarks = facemarksDetector.getLeftEyeFacemarks();
                eyes = eyeDetectionFacemarks.detect(rightEyeLandmarks, leftEyeLandmarks);
                if (eyeAspectRatio.calcEAR(rightEyeLandmarks) <= EARClosedEyeDetector.CLOSED_THRESHOLD) {
                    eyes[0] = null;
                }
                if (eyeAspectRatio.calcEAR(leftEyeLandmarks) <= EARClosedEyeDetector.CLOSED_THRESHOLD) {
                    eyes[1] = null;
                }

                sumTime += System.nanoTime() - singleStart;
                /* **************** */

                if (canShowImage()) {
                    viewsBuilder.newSection();
                    Utility.drawRects(imageMat, eyes, new Scalar(0, 255, 0), 2);
                    for (SingleEyeTestStruct expectedEye : imageToTest.eyes) {
                        if (expectedEye != null) {
                            Utility.drawRects(imageMat, new Rect[]{expectedEye.minEyeRect, expectedEye.maxEyeRect}, new Scalar(60, 130, 255), 2);
                        }
                    }
                    addImageToView(imageMat, viewsBuilder);
                    viewsBuilder.closeSection();
                }

                eyesDetectionStats(eyes, imageToTest.eyes);
            }
        }

        long end = System.nanoTime();
        double timeInSec = (end - start) / (double) 1_000_000_000;
        double sumTimeInSec = sumTime / (double) 1_000_000_000;

        viewsBuilder.newSection();
        viewsBuilder.addText("____________________");
        viewsBuilder.addText("____________________");
        viewsBuilder.addText("Tested images: " + imagesToTest.length);
        viewsBuilder.addText("Eyes open: " + totalOpenEyes / REPEAT_TEST);
        viewsBuilder.addText("Eyes closed: " + totalClosedEyes / REPEAT_TEST);
        viewsBuilder.addText("Correct detection: " + numCorrect / REPEAT_TEST);
        viewsBuilder.addText("Perfect detection: " + numPerfect / REPEAT_TEST);
        viewsBuilder.addText("Partial correct: " + numPartialCorrect / REPEAT_TEST);
        viewsBuilder.addText("3/4 side correct: " + numThreeSideCorrect / REPEAT_TEST);
        viewsBuilder.addText("Wrong detection: " + numWrong / REPEAT_TEST);
        viewsBuilder.addText("Closed wrong: " + closedWrong / REPEAT_TEST);
        viewsBuilder.addText("Open wrong: " + openWrong / REPEAT_TEST);
        viewsBuilder.addText("Closed correct: " + closedCorrect / REPEAT_TEST);
        viewsBuilder.addText("Avg side deviation: " + String.format(new Locale("pl", "PL"), "%.2f", wrongSum / wrongSideCounter * 100) + "%");
        viewsBuilder.addText("Total time: " + timeInSec + " s");
        viewsBuilder.addText("Avg test time: " + (timeInSec / REPEAT_TEST) + " s");
        viewsBuilder.addText("Detections total time: " + sumTimeInSec + " s");
        viewsBuilder.addText("Avg detections time: " + (sumTimeInSec / REPEAT_TEST) + " s");
        viewsBuilder.addText("One detection avg time: " + (sumTimeInSec / (double) imagesToTest.length / REPEAT_TEST) + " s");
        viewsBuilder.addText("____________________");
        viewsBuilder.closeSection();

        viewsBuilder.build();
    }

    private void eyesDetectionStats(Rect[] eyes, SingleEyeTestStruct[] expectedEyes) {
        eyeDetectionStats(eyes[0], expectedEyes[0]);
        eyeDetectionStats(eyes[1], expectedEyes[1]);
    }

    private void eyeDetectionStats(Rect eye, SingleEyeTestStruct expectedEye) {
        if (eye == null) {
            if (expectedEye == null) {
                numCorrect++;
                numPerfect++;
                closedCorrect++;
                totalClosedEyes++;
            } else {
                numWrong++;
                openWrong++;
                totalOpenEyes++;
            }
            return;
        }

        if (expectedEye == null) {
            numWrong++;
            closedWrong++;
            totalClosedEyes++;
            return;
        }

        totalOpenEyes++;

        double width = expectedEye.maxEyeRect.width;
        double height = expectedEye.maxEyeRect.height;

        double diffTop = -calcDetectionDeviation(eye.y, expectedEye.maxEyeRect.y, expectedEye.minEyeRect.y);
        double diffRight = calcDetectionDeviation((eye.x + eye.width), expectedEye.minEyeRect.x + expectedEye.minEyeRect.width, expectedEye.maxEyeRect.x + expectedEye.maxEyeRect.width);
        double diffBottom = calcDetectionDeviation((eye.y + eye.height), expectedEye.minEyeRect.y + expectedEye.minEyeRect.height, expectedEye.maxEyeRect.y + expectedEye.maxEyeRect.height);
        double diffLeft = -calcDetectionDeviation(eye.x, expectedEye.maxEyeRect.x, expectedEye.minEyeRect.x);

        double diffTopPercent = Math.abs(diffTop) / height;
        double diffRightPercent = Math.abs(diffRight) / width;
        double diffBottomPercent = Math.abs(diffBottom) / height;
        double diffLeftPercent = Math.abs(diffLeft) / width;

        countCorrectness(diffTopPercent, diffRightPercent, diffBottomPercent, diffLeftPercent);
    }

    private void countCorrectness(double top, double right, double bottom, double left) {
        double[] sides = new double[]{top, right, bottom, left};

        int perfectSides = 0;
        int wrongSides20Percent = 0;
        double sumPercent = 0;
        double wrongSides = 0;

        for (double side : sides) {
            int sideCorrectRate = calcSideCorrectRate(side);

            if (sideCorrectRate == 1) {
                perfectSides++;
            } else if (sideCorrectRate == 0) {
                wrongSides++;
                sumPercent += side;
            } else if (sideCorrectRate == -1) {
                if (wrongSides20Percent == 1) {
                    numWrong++;
                    return;
                }
                wrongSides20Percent = 1;
                wrongSides++;
                sumPercent += side;
            } else if (sideCorrectRate == -2) {
                numWrong++;
                return;
            }
        }

        numCorrect++;
        wrongSideCounter += wrongSides;
        wrongSum += sumPercent;

        if (perfectSides == 3) {
            numThreeSideCorrect++;
        } else if (perfectSides == 4) {
            numPerfect++;
        } else {
            numPartialCorrect++;
        }
    }

    private int calcSideCorrectRate(double diff) {
        if (diff == 0.0) {
            return 1;
        } else if (diff < 0.1) {
            return 0;
        } else if (diff < 0.2) {
            return -1;
        } else {
            return -2;
        }
    }

    private int calcDetectionDeviation(int val, int min, int max) {
        if (val >= min && val <= max) {
            return 0;
        } else if (val < min) {
            return val - min;
        } else {
            return val - max;
        }
    }

    private boolean canShowImage() {
        return SHOW_IMAGES && REPEAT_TEST == 1.0;
    }


    private static class EyesImageTestStruct {
        public int imgID;
        public SingleEyeTestStruct[] eyes;

        public EyesImageTestStruct(int imgID, SingleEyeTestStruct[] eyes) {
            this.imgID = imgID;
            this.eyes = eyes;
        }
    }

    private static class SingleEyeTestStruct {
        public Rect minEyeRect;
        public Rect maxEyeRect;

        public SingleEyeTestStruct(Rect minEyeRect, Rect maxEyeRect) {
            this.minEyeRect = minEyeRect;
            this.maxEyeRect = maxEyeRect;
        }
    }

    private final EyesImageTestStruct[] imagesToTest = {
            new EyesImageTestStruct(R.drawable.face_test_500x500_1, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(158, 143, 38, 17), new Rect(138, 115, 78, 61)),
                    new SingleEyeTestStruct(new Rect(271, 147, 34, 17), new Rect(242, 112, 94, 77))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_2, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(216, 150, 30, 17), new Rect(201, 129, 61, 53)),
                    new SingleEyeTestStruct(new Rect(302, 143, 32, 14), new Rect(281, 116, 71, 58))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_3, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(187, 183, 33, 19), new Rect(165, 156, 77, 69)),
                    new SingleEyeTestStruct(new Rect(279, 185, 31, 18), new Rect(255, 160, 76, 58))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_4, new SingleEyeTestStruct[]{
                    null,
                    new SingleEyeTestStruct(new Rect(299, 112, 28, 14), new Rect(284, 76, 59, 67))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_5, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(137, 259, 44, 23), new Rect(111, 219, 95, 82)),
                    new SingleEyeTestStruct(new Rect(255, 259, 37, 20), new Rect(229, 222, 87, 75))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_6, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(241, 119, 12, 8), new Rect(225, 106, 40, 32)),
                    new SingleEyeTestStruct(new Rect(281, 116, 12, 6), new Rect(269, 103, 39, 32))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_7, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(207, 227, 33, 18), new Rect(189, 204, 70, 60)),
                    new SingleEyeTestStruct(new Rect(300, 225, 36, 18), new Rect(281, 201, 76, 61))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_8, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(196, 193, 33, 21), new Rect(169, 153, 88, 90)),
                    new SingleEyeTestStruct(new Rect(306, 228, 38, 19), new Rect(275, 186, 109, 95))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_9, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(218, 141, 50, 28), new Rect(179, 93, 138, 103)),
                    new SingleEyeTestStruct(new Rect(363, 189, 44, 25), new Rect(336, 141, 114, 104))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_10, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(277, 169, 39, 20), new Rect(255, 136, 84, 78)),
                    new SingleEyeTestStruct(new Rect(369, 185, 35, 20), new Rect(356, 154, 66, 69))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_11, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(296, 111, 22, 21), new Rect(281, 85, 59, 62)),
                    new SingleEyeTestStruct(new Rect(344, 154, 19, 22), new Rect(332, 142, 53, 44))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_12, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(154, 171, 37, 19), new Rect(129, 135, 88, 72)),
                    new SingleEyeTestStruct(new Rect(264, 175, 35, 17), new Rect(237, 134, 85, 77))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_13, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(169, 229, 37, 18), new Rect(139, 193, 93, 79)),
                    new SingleEyeTestStruct(new Rect(294, 230, 43, 18), new Rect(264, 193, 108, 80))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_14, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(173, 108, 34, 15), new Rect(147, 78, 82, 61)),
                    new SingleEyeTestStruct(new Rect(264, 106, 32, 14), new Rect(256, 79, 59, 55))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_15, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(213, 169, 24, 13), new Rect(198, 143, 51, 56)),
                    new SingleEyeTestStruct(new Rect(275, 171, 19, 11), new Rect(265, 148, 39, 48))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_16, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(187, 211, 34, 15), new Rect(172, 189, 65, 56)),
                    new SingleEyeTestStruct(new Rect(274, 205, 27, 14), new Rect(258, 182, 65, 60))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_17, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(154, 242, 40, 20), new Rect(127, 212, 85, 72)),
                    new SingleEyeTestStruct(new Rect(268, 241, 37, 19), new Rect(237, 201, 110, 83))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_18, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(159, 248, 47, 25), new Rect(125, 205, 109, 96)),
                    new SingleEyeTestStruct(new Rect(293, 240, 47, 26), new Rect(253, 196, 118, 104))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_19, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(168, 227, 41, 20), new Rect(133, 194, 103, 75)),
                    new SingleEyeTestStruct(new Rect(290, 225, 47, 23), new Rect(270, 190, 101, 88))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_20, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(222, 123, 24, 11), new Rect(206, 107, 56, 37)),
                    new SingleEyeTestStruct(new Rect(277, 127, 19, 10), new Rect(268, 111, 42, 37))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_21, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(204, 231, 36, 21), new Rect(181, 207, 75, 64)),
                    new SingleEyeTestStruct(new Rect(300, 230, 37, 22), new Rect(289, 204, 67, 64))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_22, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(165, 225, 45, 21), new Rect(129, 190, 108, 77)),
                    new SingleEyeTestStruct(new Rect(287, 226, 50, 19), new Rect(263, 190, 105, 81))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_23, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(190, 205, 43, 21), new Rect(161, 156, 100, 94)),
                    new SingleEyeTestStruct(new Rect(323, 230, 44, 21), new Rect(298, 182, 97, 93))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_24, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(196, 197, 27, 13), new Rect(183, 176, 56, 51)),
                    new SingleEyeTestStruct(new Rect(283, 198, 27, 13), new Rect(265, 180, 62, 49))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_25, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(225, 147, 27, 14), new Rect(213, 118, 54, 56)),
                    new SingleEyeTestStruct(new Rect(291, 160, 24, 11), new Rect(279, 132, 53, 53))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_26, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(155, 212, 38, 17), new Rect(122, 180, 91, 81)),
                    new SingleEyeTestStruct(new Rect(265, 207, 40, 18), new Rect(240, 175, 101, 78))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_27, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(176, 194, 36, 21), new Rect(155, 159, 82, 81)),
                    new SingleEyeTestStruct(new Rect(287, 188, 36, 21), new Rect(259, 152, 94, 84))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_28, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(78, 162, 40, 22), new Rect(56, 126, 73, 80)),
                    new SingleEyeTestStruct(new Rect(189, 162, 44, 22), new Rect(167, 125, 90, 87))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_29, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(165, 226, 44, 22), new Rect(133, 184, 101, 90)),
                    new SingleEyeTestStruct(new Rect(291, 226, 45, 20), new Rect(270, 192, 97, 83))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_30, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(129, 151, 26, 16), new Rect(114, 130, 53, 51)),
                    new SingleEyeTestStruct(new Rect(197, 150, 25, 15), new Rect(182, 128, 55, 54))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_31, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(206, 154, 18, 11), new Rect(192, 140, 40, 38)),
                    new SingleEyeTestStruct(new Rect(254, 154, 20, 10), new Rect(244, 135, 46, 42))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_32, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(193, 135, 30, 17), new Rect(178, 103, 57, 67)),
                    new SingleEyeTestStruct(new Rect(268, 142, 32, 17), new Rect(248, 106, 84, 73))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_33, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(99, 162, 33, 18), new Rect(75, 106, 81, 103)),
                    new SingleEyeTestStruct(new Rect(212, 186, 47, 16), new Rect(191, 128, 97, 104))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_34, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(179, 204, 33, 11), new Rect(156, 178, 83, 61)),
                    new SingleEyeTestStruct(new Rect(284, 202, 32, 11), new Rect(256, 179, 85, 61))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_35, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(164, 266, 41, 18), new Rect(137, 231, 93, 82)),
                    new SingleEyeTestStruct(new Rect(275, 245, 39, 21), new Rect(259, 207, 90, 92))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_36, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(140, 187, 27, 10), new Rect(121, 156, 65, 66)),
                    new SingleEyeTestStruct(new Rect(226, 168, 27, 11), new Rect(203, 137, 71, 67))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_37, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(163, 227, 45, 20), new Rect(126, 191, 113, 87)),
                    new SingleEyeTestStruct(new Rect(291, 228, 41, 16), new Rect(270, 190, 91, 82))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_38, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(166, 228, 39, 14), new Rect(125, 182, 107, 92)),
                    new SingleEyeTestStruct(new Rect(294, 229, 40, 14), new Rect(261, 187, 113, 89))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_39, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(280, 122, 22, 11), new Rect(266, 104, 44, 42)),
                    new SingleEyeTestStruct(new Rect(334, 135, 23, 11), new Rect(320, 120, 49, 40))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_40, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(195, 193, 24, 13), new Rect(179, 176, 54, 49)),
                    new SingleEyeTestStruct(new Rect(262, 180, 25, 13), new Rect(247, 156, 65, 58))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_41, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(151, 217, 51, 20), new Rect(112, 170, 120, 99)),
                    new SingleEyeTestStruct(new Rect(301, 215, 46, 19), new Rect(265, 164, 120, 98))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_42, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(171, 103, 40, 21), new Rect(139, 66, 98, 86)),
                    new SingleEyeTestStruct(new Rect(286, 111, 36, 20), new Rect(258, 74, 94, 83))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_43, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(168, 230, 39, 14), new Rect(126, 184, 112, 97)),
                    new SingleEyeTestStruct(new Rect(293, 231, 38, 11), new Rect(256, 181, 125, 100))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_44, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(196, 181, 34, 16), new Rect(163, 142, 90, 81)),
                    new SingleEyeTestStruct(new Rect(286, 181, 35, 15), new Rect(264, 141, 88, 77))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_45, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(223, 145, 27, 15), new Rect(200, 114, 77, 65)),
                    new SingleEyeTestStruct(new Rect(298, 167, 28, 11), new Rect(284, 137, 69, 61))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_46, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(125, 204, 45, 22), new Rect(94, 166, 100, 88)),
                    new SingleEyeTestStruct(new Rect(241, 195, 45, 20), new Rect(209, 153, 118, 93))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_47, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(165, 229, 40, 15), new Rect(127, 188, 113, 91)),
                    new SingleEyeTestStruct(new Rect(290, 228, 44, 14), new Rect(263, 189, 105, 88))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_48, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(206, 201, 39, 18), new Rect(182, 167, 86, 77)),
                    new SingleEyeTestStruct(new Rect(314, 196, 36, 17), new Rect(286, 160, 91, 78))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_49, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(195, 251, 40, 15), new Rect(169, 210, 98, 88)),
                    new SingleEyeTestStruct(new Rect(315, 257, 37, 14), new Rect(287, 223, 98, 78))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_50, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(166, 176, 48, 24), new Rect(121, 121, 131, 110)),
                    new SingleEyeTestStruct(new Rect(311, 208, 51, 22), new Rect(277, 148, 132, 109))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_51, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(204, 304, 20, 7), new Rect(187, 283, 54, 46)),
                    new SingleEyeTestStruct(new Rect(260, 308, 20, 8), new Rect(244, 286, 52, 42))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_52, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(207, 221, 22, 7), new Rect(188, 197, 65, 50)),
                    new SingleEyeTestStruct(new Rect(267, 232, 20, 9), new Rect(251, 209, 52, 49))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_53, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(210, 258, 14, 10), new Rect(197, 236, 38, 46)),
                    new SingleEyeTestStruct(new Rect(254, 251, 20, 10), new Rect(235, 226, 51, 52))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_55, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(151, 230, 20, 8), new Rect(139, 214, 45, 34)),
                    new SingleEyeTestStruct(new Rect(196, 237, 15, 8), new Rect(183, 220, 42, 37))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_56, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(202, 214, 24, 10), new Rect(187, 193, 51, 45)),
                    new SingleEyeTestStruct(new Rect(262, 215, 21, 8), new Rect(244, 193, 55, 45))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_58, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(155, 254, 24, 9), new Rect(136, 231, 60, 48)),
                    new SingleEyeTestStruct(new Rect(218, 265, 22, 9), new Rect(200, 239, 57, 51))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_59, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(137, 167, 21, 13), new Rect(125, 145, 46, 50)),
                    new SingleEyeTestStruct(new Rect(193, 166, 27, 13), new Rect(178, 142, 58, 53))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_60, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(130, 226, 35, 11), new Rect(108, 193, 79, 70)),
                    new SingleEyeTestStruct(new Rect(227, 227, 32, 13), new Rect(198, 188, 84, 74))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_61, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(133, 242, 33, 19), new Rect(112, 204, 75, 74)),
                    new SingleEyeTestStruct(new Rect(225, 247, 32, 18), new Rect(204, 209, 75, 77))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_62, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(218, 166, 22, 8), new Rect(208, 147, 43, 38)),
                    new SingleEyeTestStruct(new Rect(272, 169, 15, 7), new Rect(259, 148, 40, 38))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_63, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(149, 187, 15, 12), new Rect(138, 171, 32, 40)),
                    new SingleEyeTestStruct(new Rect(186, 171, 18, 11), new Rect(169, 149, 47, 46))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_64, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(146, 214, 20, 7), new Rect(130, 191, 51, 46)),
                    new SingleEyeTestStruct(new Rect(205, 212, 20, 8), new Rect(189, 189, 51, 45))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_65, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(197, 241, 38, 16), new Rect(169, 211, 84, 69)),
                    new SingleEyeTestStruct(new Rect(298, 241, 36, 13), new Rect(270, 206, 87, 70))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_66, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(224, 260, 26, 14), new Rect(200, 230, 68, 63)),
                    new SingleEyeTestStruct(new Rect(291, 231, 24, 13), new Rect(269, 204, 61, 55))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_67, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(104, 198, 47, 23), new Rect(77, 154, 108, 98)),
                    new SingleEyeTestStruct(new Rect(250, 232, 40, 18), new Rect(212, 180, 118, 98))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_68, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(157, 229, 24, 10), new Rect(140, 205, 61, 48)),
                    new SingleEyeTestStruct(new Rect(223, 247, 25, 11), new Rect(209, 217, 58, 60))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_69, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(151, 225, 26, 9), new Rect(133, 200, 62, 50)),
                    new SingleEyeTestStruct(new Rect(224, 229, 27, 10), new Rect(210, 200, 59, 56))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_70, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(137, 239, 28, 12), new Rect(118, 213, 69, 50)),
                    new SingleEyeTestStruct(new Rect(214, 259, 27, 12), new Rect(201, 232, 61, 53))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_71, new SingleEyeTestStruct[]{
                    null,
                    new SingleEyeTestStruct(new Rect(295, 170, 24, 11), new Rect(277, 148, 63, 49))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_72, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(204, 199, 34, 17), new Rect(183, 159, 65, 76)),
                    null}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_73, new SingleEyeTestStruct[]{
                    null,
                    new SingleEyeTestStruct(new Rect(307, 209, 45, 14), new Rect(287, 177, 94, 71))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_74, new SingleEyeTestStruct[]{
                    null,
                    null}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_75, new SingleEyeTestStruct[]{
                    null,
                    null}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_76, new SingleEyeTestStruct[]{
                    null,
                    null}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_77, new SingleEyeTestStruct[]{
                    null,
                    null}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_78, new SingleEyeTestStruct[]{
                    null,
                    new SingleEyeTestStruct(new Rect(306, 229, 33, 15), new Rect(285, 199, 74, 65))}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_79, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(173, 209, 31, 15), new Rect(149, 168, 74, 80)),
                    null}),
            new EyesImageTestStruct(R.drawable.face_test_500x500_80, new SingleEyeTestStruct[]{
                    null,
                    new SingleEyeTestStruct(new Rect(306, 120, 23, 9), new Rect(295, 104, 46, 37))}),
    };
}
