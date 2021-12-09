package org.pw.engithesis.androidcameracontrol.tests;

import android.content.Context;
import android.widget.ScrollView;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.Utility;
import org.pw.engithesis.androidcameracontrol.ViewsBuilder;
import org.pw.engithesis.androidcameracontrol.detectors.FaceDetector;

import java.util.Locale;

public class FaceDetectorImageTest extends ImageTest {

    private static final boolean DETECT_IN_GRAY = false;
    private static final double REPEAT_TEST = 1.0;
    private static final boolean SHOW_IMAGES = true; // show images in ui. Works only if REPEAT_TEST == 1

    private final FaceDetector faceDetector = new FaceDetector();

    private int numCorrect = 0;
    private int numPerfect = 0;
    private int numPartialCorrect = 0;
    private int numThreeSideCorrect = 0;
    private int numWrong = 0;
    private double wrongSum = 0;
    private int wrongSideCounter = 0;


    protected FaceDetectorImageTest(Context context, ScrollView parent) {
        super(context, parent);
    }

    @Override
    public void createView() {
        ViewsBuilder viewsBuilder = new ViewsBuilder(ctx, parentView);

        long sumTime = 0;
        long start = System.nanoTime();
        for (int j = 0; j < REPEAT_TEST; j++) {
            for (FaceDetectorImageTestStruct imageToTest : imagesToTest) {
                Rect[] expectedFace = new Rect[]{imageToTest.minFaceRect, imageToTest.maxFaceRect};

                Mat imageMat = getImageMat(imageToTest.imgID);
                Rect face;

                if (!DETECT_IN_GRAY) {
                    long singleStart = System.nanoTime();
                    face = faceDetector.detect(imageMat);
                    sumTime += System.nanoTime() - singleStart;
                } else {
                    Mat grayImage = new Mat();
                    Imgproc.cvtColor(imageMat, grayImage, Imgproc.COLOR_RGB2GRAY);
                    long singleStart = System.nanoTime();
                    face = faceDetector.detect(grayImage);
                    sumTime += System.nanoTime() - singleStart;
                }

                if (canShowImage()) {
                    viewsBuilder.newSection();
                    Utility.drawRect(imageMat, face, new Scalar(0, 255, 0), 2);
                    Utility.drawRects(imageMat, expectedFace, new Scalar(60, 130, 255), 2);
                    Utility.drawVerticalLine(imageMat, (int) (imageMat.width() * FaceDetector.LEFT_BOUNDARY));
                    Utility.drawVerticalLine(imageMat, (int) (imageMat.width() * FaceDetector.RIGHT_BOUNDARY));
                    addImageToView(imageMat, viewsBuilder);
                    faceDetectionStats(face, expectedFace, viewsBuilder, true);
                    viewsBuilder.closeSection();
                } else {
                    faceDetectionStats(face, expectedFace, viewsBuilder, false);
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
        viewsBuilder.addText("Correct detection: " + numCorrect / REPEAT_TEST);
        viewsBuilder.addText("Perfect detection: " + numPerfect / REPEAT_TEST);
        viewsBuilder.addText("Partial correct: " + numPartialCorrect / REPEAT_TEST);
        viewsBuilder.addText("3/4 side correct: " + numThreeSideCorrect / REPEAT_TEST);
        viewsBuilder.addText("Wrong detection: " + numWrong / REPEAT_TEST);
        viewsBuilder.addText("Avg side deviation: " + String.format(Locale.getDefault(), "%.2f", wrongSum / wrongSideCounter / REPEAT_TEST * 100) + "%");
        viewsBuilder.addText("Total tests time: " + timeInSec + " s");
        viewsBuilder.addText("Avg test time: " + (timeInSec / REPEAT_TEST) + " s");
        viewsBuilder.addText("Detections total time: " + sumTimeInSec + " s");
        viewsBuilder.addText("Avg detections time: " + (sumTimeInSec / REPEAT_TEST) + " s");
        viewsBuilder.addText("One detection avg time: " + (sumTimeInSec / (double) imagesToTest.length / REPEAT_TEST) + " s");
        viewsBuilder.addText("____________________");
        viewsBuilder.closeSection();

        viewsBuilder.build();
    }

    private void faceDetectionStats(Rect face, Rect[] expectedFace, ViewsBuilder viewsBuilder, boolean showImgStats) {
        if (face == null) {
            numWrong++;
            return;
        }
        double width = expectedFace[1].width;
        double height = expectedFace[1].height;

        double diffTop = -calcDetectionDeviation(face.y, expectedFace[1].y, expectedFace[0].y);
        double diffRight = calcDetectionDeviation((face.x + face.width), expectedFace[0].x + expectedFace[0].width, expectedFace[1].x + expectedFace[1].width);
        double diffBottom = calcDetectionDeviation((face.y + face.height), expectedFace[0].y + expectedFace[0].height, expectedFace[1].y + expectedFace[1].height);
        double diffLeft = -calcDetectionDeviation(face.x, expectedFace[1].x, expectedFace[0].x);

        double diffTopPercent = Math.abs(diffTop) / height;
        double diffRightPercent = Math.abs(diffRight) / width;
        double diffBottomPercent = Math.abs(diffBottom) / height;
        double diffLeftPercent = Math.abs(diffLeft) / width;

        if (showImgStats) {
            viewsBuilder.addText(prepareImageStatText("Top", diffTop, diffTopPercent));
            viewsBuilder.addText(prepareImageStatText("Right", diffRight, diffRightPercent));
            viewsBuilder.addText(prepareImageStatText("Bottom", diffBottom, diffBottomPercent));
            viewsBuilder.addText(prepareImageStatText("Left", diffLeft, diffLeftPercent));
        }

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

    private String prepareImageStatText(String side, double diff, double diffPercent) {
        return side + ": " + (int) diff + " (" + String.format(new Locale("pl", "PL"), "%.2f", diffPercent * 100) + "%)";
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


    private static class FaceDetectorImageTestStruct {
        public int imgID;
        public Rect minFaceRect;
        public Rect maxFaceRect;

        public FaceDetectorImageTestStruct(int imgID, Rect minFaceRect, Rect maxFaceRect) {
            this.imgID = imgID;
            this.minFaceRect = minFaceRect;
            this.maxFaceRect = maxFaceRect;
        }
    }

    private final FaceDetectorImageTestStruct[] imagesToTest = {
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_1, new Rect(137, 93, 203, 203), new Rect(95, 42, 292, 305)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_2, new Rect(201, 115, 157, 144), new Rect(166, 74, 235, 244)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_3, new Rect(163, 134, 174, 173), new Rect(121, 82, 255, 282)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_4, new Rect(196, 49, 140, 162), new Rect(158, 2, 200, 266)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_5, new Rect(111, 203, 200, 211), new Rect(70, 106, 271, 369)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_6, new Rect(226, 103, 82, 72), new Rect(200, 40, 141, 167)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_7, new Rect(192, 192, 166, 169), new Rect(166, 106, 220, 316)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_8, new Rect(165, 159, 215, 228), new Rect(116, 71, 313, 381)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_9, new Rect(162, 83, 291, 302), new Rect(75, 0, 406, 500)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_10, new Rect(260, 129, 158, 184), new Rect(169, 36, 284, 330)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_11, new Rect(262, 81, 126, 136), new Rect(217, 44, 176, 198)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_12, new Rect(132, 124, 188, 205), new Rect(110, 31, 243, 338)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_13, new Rect(138, 194, 238, 200), new Rect(110, 79, 318, 386)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_14, new Rect(144, 75, 165, 158), new Rect(115, 0, 212, 260)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_15, new Rect(195, 143, 109, 122), new Rect(128, 111, 189, 218)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_16, new Rect(166, 182, 160, 142), new Rect(143, 106, 201, 264)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_17, new Rect(133, 201, 216, 205), new Rect(99, 81, 289, 364)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_18, new Rect(125, 200, 247, 225), new Rect(64, 75, 380, 419)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_19, new Rect(134, 190, 234, 211), new Rect(117, 102, 289, 349)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_20, new Rect(205, 103, 103, 97), new Rect(181, 59, 137, 179)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_21, new Rect(182, 203, 177, 152), new Rect(150, 177, 224, 221)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_22, new Rect(128, 182, 240, 216), new Rect(58, 71, 351, 408)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_23, new Rect(166, 152, 229, 261), new Rect(119, 107, 304, 366)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_24, new Rect(176, 175, 149, 143), new Rect(128, 101, 237, 270)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_25, new Rect(213, 115, 117, 12), new Rect(187, 81, 162, 198)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_26, new Rect(117, 173, 229, 207), new Rect(80, 47, 315, 409)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_27, new Rect(146, 157, 212, 204), new Rect(116, 61, 288, 373)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_28, new Rect(57, 125, 201, 190), new Rect(45, 14, 281, 345)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_29, new Rect(129, 188, 238, 222), new Rect(45, 56, 364, 420)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_30, new Rect(111, 128, 133, 132), new Rect(85, 66, 182, 228)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_31, new Rect(194, 135, 95, 90), new Rect(184, 96, 132, 153)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_32, new Rect(181, 101, 157, 154), new Rect(163, 32, 200, 266)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_33, new Rect(67, 102, 235, 254), new Rect(32, 17, 331, 406)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_34, new Rect(153, 178, 186, 161), new Rect(123, 80, 242, 300)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_35, new Rect(126, 200, 223, 235), new Rect(83, 88, 293, 389)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_36, new Rect(116, 136, 161, 157), new Rect(96, 76, 204, 260)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_37, new Rect(130, 187, 228, 212), new Rect(81, 79, 294, 390)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_38, new Rect(124, 182, 247, 227), new Rect(59, 74, 340, 403)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_39, new Rect(270, 107, 98, 98), new Rect(256, 65, 118, 168)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_40, new Rect(181, 156, 125, 127), new Rect(168, 106, 168, 207)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_41, new Rect(118, 165, 263, 251), new Rect(84, 41, 327, 439)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_42, new Rect(145, 64, 203, 200), new Rect(108, 0, 266, 315)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_43, new Rect(133, 187, 231, 230), new Rect(88, 69, 347, 426)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_44, new Rect(165, 145, 184, 160), new Rect(140, 80, 240, 278)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_45, new Rect(203, 117, 146, 152), new Rect(146, 64, 212, 242)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_46, new Rect(108, 158, 212, 208), new Rect(98, 55, 304, 362)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_47, new Rect(128, 185, 238, 233), new Rect(86, 80, 310, 413)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_48, new Rect(181, 163, 192, 178), new Rect(150, 96, 254, 286)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_49, new Rect(166, 220, 213, 171), new Rect(93, 110, 304, 349)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_50, new Rect(138, 120, 260, 273), new Rect(92, 6, 337, 465)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_51, new Rect(191, 287, 99, 91), new Rect(157, 228, 147, 194)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_52, new Rect(198, 199, 100, 102), new Rect(145, 138, 165, 202)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_53, new Rect(201, 231, 86, 97), new Rect(194, 181, 157, 184)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_55, new Rect(136, 216, 83, 79), new Rect(109, 166, 119, 158)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_56, new Rect(187, 192, 107, 100), new Rect(169, 134, 149, 194)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_58, new Rect(140, 238, 113, 108), new Rect(109, 174, 162, 207)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_59, new Rect(131, 143, 101, 118), new Rect(124, 90, 153, 199)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_60, new Rect(112, 189, 165, 172), new Rect(87, 109, 212, 309)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_61, new Rect(114, 201, 159, 167), new Rect(84, 115, 205, 291)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_62, new Rect(210, 145, 86, 82), new Rect(181, 104, 133, 159)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_63, new Rect(140, 149, 78, 97), new Rect(132, 117, 141, 156)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_64, new Rect(132, 187, 107, 107), new Rect(108, 133, 152, 196)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_65, new Rect(178, 198, 175, 178), new Rect(140, 111, 241, 340)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_66, new Rect(204, 205, 134, 134), new Rect(166, 162, 205, 227)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_67, new Rect(84, 150, 239, 279), new Rect(39, 55, 309, 449)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_68, new Rect(143, 199, 119, 124), new Rect(111, 147, 177, 237)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_69, new Rect(137, 196, 130, 131), new Rect(107, 144, 173, 235)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_70, new Rect(119, 209, 144, 144), new Rect(84, 141, 194, 274)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_71, new Rect(210, 149, 125, 129), new Rect(191, 85, 165, 224)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_72, new Rect(191, 154, 164, 214), new Rect(181, 58, 288, 356)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_73, new Rect(168, 176, 214, 186), new Rect(136, 69, 298, 360)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_74, new Rect(217, 156, 116, 123), new Rect(193, 89, 168, 220)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_75, new Rect(132, 217, 196, 182), new Rect(77, 65, 293, 398)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_76, new Rect(159, 196, 182, 177), new Rect(117, 105, 264, 323)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_77, new Rect(174, 167, 164, 147), new Rect(148, 89, 221, 280)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_78, new Rect(191, 200, 163, 181), new Rect(167, 113, 223, 312)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_79, new Rect(149, 167, 172, 184), new Rect(137, 54, 274, 372)),
            new FaceDetectorImageTestStruct(R.drawable.face_test_500x500_80, new Rect(240, 97, 102, 109), new Rect(213, 41, 138, 193)),
    };
}
