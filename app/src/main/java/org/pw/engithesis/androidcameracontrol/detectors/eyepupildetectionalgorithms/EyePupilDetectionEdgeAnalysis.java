package org.pw.engithesis.androidcameracontrol.detectors.eyepupildetectionalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.pw.engithesis.androidcameracontrol.Utility;

public class EyePupilDetectionEdgeAnalysis extends EyePupilDetectionAlgorithm {
    private static final double MIN_SPACE_BETWEEN_EXTREMA = 0.10;
    private static final Size GAUSSIAN_BLUR_SIZE = new Size(3, 3);
    private static final int CANNY_MIN_THRESHOLD = 100;
    private static final int CANNY_MAX_THRESHOLD = 200;

    private byte[] cannyMatBytes;
    private int width;
    private int height;

    @Override
    public Point detect(Mat frame, Rect eyeRegion) {
        Mat grayEye = getGrayEyeMat(frame, eyeRegion);
        width = grayEye.width();
        height = grayEye.height();

        Imgproc.GaussianBlur(grayEye, grayEye, GAUSSIAN_BLUR_SIZE, 0);

        Mat cannyMat = new Mat();
        Imgproc.Canny(grayEye, cannyMat, CANNY_MIN_THRESHOLD, CANNY_MAX_THRESHOLD, 3);
        cannyMatBytes = Utility.matToByteArray(cannyMat);

        int[] verticalCounters = calcVerticalCounters();
        int[] horizontalCounters = calcHorizontalCounters();

        int[] verticalExtrema = getExtrema(verticalCounters);
        int[] horizontalExtrema = getExtrema(horizontalCounters);

        Point center = calcCenter(verticalExtrema, horizontalExtrema);
        return calcPointInFrame(center, eyeRegion);
    }

    private Point calcCenter(int[] verticalExtrema, int[] horizontalExtrema) {
        int x1 = verticalExtrema[0];
        int y1 = horizontalExtrema[0];
        int x2 = verticalExtrema[1];
        int y2 = horizontalExtrema[1];

        return new Point((int) ((x1 + x2) / 2.0), (int) ((y1 + y2) / 2.0));
    }

    private int[] getExtrema(int[] counters) {
        int max1 = 0;
        int max1Index = 0;
        int minSpace = (int) (counters.length * MIN_SPACE_BETWEEN_EXTREMA);

        for (int i = 0; i < counters.length; i++) {
            if (max1 < counters[i]) {
                max1 = counters[i];
                max1Index = i;
            }
        }

        int max2 = 0;
        int max2Index = 0;

        for (int i = 0; i < counters.length; i++) {
            int space = Math.abs(max1Index - i);
            if (max2 < counters[i] && space >= minSpace) {
                max2 = counters[i];
                max2Index = i;
            }
        }

        return new int[]{max1Index, max2Index};
    }

    private int[] calcHorizontalCounters() {
        int[] rows = new int[height];
        for (int y = 0; y < height; y++) {
            rows[y] = calcCounter(y * width, 1, width);
        }
        return rows;
    }

    private int[] calcVerticalCounters() {
        int[] cols = new int[width];
        for (int x = 0; x < width; x++) {
            cols[x] = calcCounter(x, width, height);
        }
        return cols;
    }

    private int calcCounter(int startIndex, int step, int length) {
        int counter = 0;
        int index = startIndex;
        for (int i = 0; i < length; i++) {
            if (cannyMatBytes[index] == -1) {
                counter++;
            }
            index += step;
        }

        return counter;
    }
}
