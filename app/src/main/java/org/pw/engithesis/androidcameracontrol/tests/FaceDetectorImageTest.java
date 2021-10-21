package org.pw.engithesis.androidcameracontrol.tests;

import android.content.Context;
import android.widget.ScrollView;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.pw.engithesis.androidcameracontrol.FaceDetector;
import org.pw.engithesis.androidcameracontrol.MatToFile;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.Utility;
import org.pw.engithesis.androidcameracontrol.ViewsBuilder;

import java.util.Locale;

public class FaceDetectorImageTest extends ImageTest {
    private final FaceDetectorImageTestStruct[] imagesToTest = {
            new FaceDetectorImageTestStruct(R.drawable.lenna, new Rect(230, 225, 125, 150), new Rect(175, 160, 205, 250)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_1, new Rect(285, 120, 310, 290), new Rect(230, 15, 415, 500)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_2, new Rect(145, 290, 320, 390), new Rect(75, 135, 450, 660)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_3, new Rect(250, 110, 94, 94), new Rect(234, 75, 130, 150)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_4, new Rect(190, 75, 85, 110), new Rect(175, 50, 130, 155)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_5, new Rect(205, 105, 220, 230), new Rect(150, 30, 320, 370)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_6, new Rect(140, 160, 300, 310), new Rect(80, 27, 430, 510)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_7, new Rect(250, 155, 115, 130), new Rect(217, 110, 180, 210)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_8, new Rect(695, 275, 275, 300), new Rect(640, 140, 390, 500)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_9, new Rect(210, 265, 295, 355), new Rect(180, 145, 425, 570)),
    };
    private final FaceDetector faceDetector;

    /********************/
    private int numCorrect;
    private int numPartialCorrect;
    private int numThreeSideCorrect;
    private int numWrong;

    /********************/

    protected FaceDetectorImageTest(Context context, ScrollView parent) {
        super(context, parent);
        faceDetector = new FaceDetector();
    }

    @Override
    public void createView() {
        ViewsBuilder viewsBuilder = new ViewsBuilder(ctx, parentView);

        MatToFile matToFile = new MatToFile();

        int i = 0;
        for (FaceDetectorImageTestStruct imageToTest : imagesToTest) {
            viewsBuilder.newSection();

            Rect[] expectedFace = new Rect[]{imageToTest.minFaceRect, imageToTest.maxFaceRect};

            Mat imageMat = getImageMat(imageToTest.imgID);
            Rect face = faceDetector.detect(imageMat);
            Utility.drawRects(imageMat, new Rect[]{face});
            Utility.drawRects(imageMat, expectedFace, new Scalar(60, 130, 255));
            Utility.drawVerticalLine(imageMat, (int) (imageMat.width() * FaceDetector.LEFT_BOUNDARY));
            Utility.drawVerticalLine(imageMat, (int) (imageMat.width() * FaceDetector.RIGHT_BOUNDARY));

            addImageToView(imageMat, viewsBuilder);
            faceDetectionStats(face, expectedFace, viewsBuilder);

            //matToFile.saveRGBMatAsPNG("faceDetector-" + i, imageMat);
            i++;
        }

        viewsBuilder.addText("Tested images: " + imagesToTest.length);
        viewsBuilder.addText("Correct detection: " + numCorrect);
        viewsBuilder.addText("Partial correct: " + numPartialCorrect);
        viewsBuilder.addText("3/4 side correct: " + numThreeSideCorrect);
        viewsBuilder.addText("wrong detection: " + numWrong);

        viewsBuilder.build();
    }

    private static class FaceDetectorImageTestStruct {
        public int imgID;
        public Rect minFaceRect;
        public Rect maxFaceRect;

        public FaceDetectorImageTestStruct(int id, Rect minFaceRect, Rect maxFaceRect) {
            imgID = id;
            this.minFaceRect = minFaceRect;
            this.maxFaceRect = maxFaceRect;
        }
    }

    private void faceDetectionStats(Rect face, Rect[] expectedFace, ViewsBuilder viewsBuilder) {
        double width = expectedFace[1].width;
        double height = expectedFace[1].height;

        double diffTop = -calcDistanceBetween(face.y, expectedFace[1].y, expectedFace[0].y);
        double diffTopPercent = Math.abs(diffTop) / height;
        double diffRight = calcDistanceBetween((face.x + face.width), expectedFace[0].x + expectedFace[0].width, expectedFace[1].x + expectedFace[1].width);
        double diffRightPercent = Math.abs(diffRight) / width;
        double diffBottom = calcDistanceBetween((face.y + face.height), expectedFace[0].y + expectedFace[0].height, expectedFace[1].y + expectedFace[1].height);
        double diffBottomPercent = Math.abs(diffBottom) / height;
        double diffLeft = -calcDistanceBetween(face.x, expectedFace[1].x, expectedFace[0].x);
        double diffLeftPercent = Math.abs(diffLeft) / width;

        viewsBuilder.addText(statsText("Top", diffTop, diffTopPercent));
        viewsBuilder.addText(statsText("Right", diffRight, diffRightPercent));
        viewsBuilder.addText(statsText("Bottom", diffBottom, diffBottomPercent));
        viewsBuilder.addText(statsText("Left", diffLeft, diffLeftPercent));

        countCorrectness(diffTopPercent, diffRightPercent, diffBottomPercent, diffLeftPercent);
    }

    private void countCorrectness(double top, double right, double bottom, double left) {
        double[] sides = new double[]{top, right, bottom, left};

        int correctCounter = 0;
        int wrong = 0;

        for (double side : sides) {
            int temp = sideCorrect(side);
            if (temp == -2) {
                numWrong++;
                return;
            } else if (temp == -1) {
                if (wrong == 1) {
                    numWrong++;
                    return;
                }
                wrong = 1;
            } else if (temp == 1) {
                correctCounter++;
            }
        }

        if (correctCounter == 4) {
            numCorrect++;
        } else if (correctCounter > 0) {
            numPartialCorrect++;
            if (correctCounter == 3) {
                numThreeSideCorrect++;
            }
        } else {
            numWrong++;
        }
    }

    private int sideCorrect(double diff) {
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


    private String statsText(String side, double diff, double diffPercent) {
        return side + ": " + (int) diff + " (" + String.format(Locale.getDefault(), "%.2f", diffPercent * 100) + "%)";
    }

    private int calcDistanceBetween(int val, int min, int max) {
        if (val >= min && val <= max) {
            return 0;
        } else if (val < min) {
            return val - min;
        } else {
            return val - max;
        }
    }
}
