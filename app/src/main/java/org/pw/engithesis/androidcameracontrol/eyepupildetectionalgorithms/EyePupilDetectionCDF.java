package org.pw.engithesis.androidcameracontrol.eyepupildetectionalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.pw.engithesis.androidcameracontrol.Utility;
import org.pw.engithesis.androidcameracontrol.thresholding.CDFLuminanceThresholding;
import org.pw.engithesis.androidcameracontrol.thresholding.IntensityThresholding;

public class EyePupilDetectionCDF implements EyePupilDetectionAlgorithm {
    private final CDFLuminanceThresholding cdfThresholding;
    private final IntensityThresholding intensityThresholding;
    private final Mat erodeKernel;
    private byte[] grayEyePixels;

    public EyePupilDetectionCDF() {
        cdfThresholding = new CDFLuminanceThresholding();
        erodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(2, 2));
        intensityThresholding = new IntensityThresholding();
    }

    @Override
    public Point detect(Mat frame, Rect eyeRegion) {
        Mat grayEyeMat = getGrayEyeMat(frame, eyeRegion);
        grayEyePixels = null;
        grayEyePixels = Utility.matToByteArray(grayEyeMat);
        int width = grayEyeMat.width();
        int height = grayEyeMat.height();

        Mat binaryEyeCDF = cdfThresholding.thresholdNewMat(grayEyeMat);
        Imgproc.erode(binaryEyeCDF, binaryEyeCDF, erodeKernel);

        Point darkestPixel = getDarkestPixel(binaryEyeCDF);
        double avgIntensity = calcAvgIntensity(darkestPixel, width, height);

        Mat erodedGrayEyeMat = new Mat();
        Imgproc.erode(grayEyeMat, erodedGrayEyeMat, erodeKernel);

        Rect darkestPixelNeighborhood = getPMIRect((int) darkestPixel.x, (int) darkestPixel.y, width, height);
        intensityThresholding.setIntensity(avgIntensity);
        Mat darkestPixelNeighborhoodMat = erodedGrayEyeMat.submat(darkestPixelNeighborhood);
        intensityThresholding.thresholdRef(darkestPixelNeighborhoodMat);

        Point gravityCenter = calcGravityCenter(darkestPixelNeighborhoodMat);
        return calcPointInFrame(gravityCenter, darkestPixelNeighborhood, eyeRegion);
    }

    private Mat getGrayEyeMat(Mat frame, Rect eyeRegion) {
        Mat eyeMat = frame.submat(eyeRegion);
        Mat grayEyeMat = new Mat();
        Imgproc.cvtColor(eyeMat, grayEyeMat, Imgproc.COLOR_RGB2GRAY);

        return grayEyeMat;
    }

    private Point getDarkestPixel(Mat erodedMat) {
        int length = (int) erodedMat.total();
        byte[] erodedPixels = Utility.matToByteArray(erodedMat);

        int darkestPixelIndex = 0;
        int darkestPixelLuminance = 255;

        for (int i = 0; i < length; i++) {
            if (erodedPixels[i] == -1) // is white on eroded mat
            {
                int value = grayEyePixels[i] & 0xFF;
                if (value < darkestPixelLuminance) {
                    darkestPixelLuminance = value;
                    darkestPixelIndex = i;
                }
            }
        }

        return Utility.indexToPoint(darkestPixelIndex, erodedMat.width());
    }

    private double calcAvgIntensity(Point anchor, int width, int height) {
        int top = Utility.clamp((int) anchor.y - 4, 0, height - 1);
        int bottom = Utility.clamp((int) anchor.y + 5, 0, height - 1);
        int left = Utility.clamp((int) anchor.x - 4, 0, width - 1);
        int right = Utility.clamp((int) anchor.x + 5, 0, width - 1);

        double avgIntensity = 0.0;

        for (int y = top; y <= bottom; y++) {
            for (int x = left; x <= right; x++) {
                int index = y * width + x;
                int pixelValue = grayEyePixels[index] & 0xFF;
                avgIntensity += pixelValue;
            }
        }

        avgIntensity /= ((bottom - top) * (right - left));

        return avgIntensity;
    }

    private Rect getPMIRect(int x, int y, int width, int height) {
        width -= 1;
        height -= 1;

        int rectLeft = Utility.clamp(x - 7, 0, width);
        int rectTop = Utility.clamp(y - 7, 0, height);
        int rectRight = Utility.clamp(x + 7, 0, width);
        int rectBottom = Utility.clamp(y + 7, 0, height);

        int rectWidth = rectRight - rectLeft;
        int rectHeight = rectBottom - rectTop;

        return new Rect(rectLeft, rectTop, rectWidth, rectHeight);
    }

    private Point calcGravityCenter(Mat mat) {
        int pixelNum = (int) mat.total();
        int width = mat.width();
        byte[] pixels = Utility.matToByteArray(mat);

        int rowSum = 0;
        int colSum = 0;
        int whitePixelCount = 0;

        for (int i = 0; i < pixelNum; i++) {
            if (pixels[i] == -1) {
                Point point = Utility.indexToPoint(i, width);
                rowSum += point.y;
                colSum += point.x;
                whitePixelCount++;
            }
        }

        int x = rowSum / whitePixelCount;
        int y = colSum / whitePixelCount;

        return new Point(x, y);
    }

    private Point calcPointInFrame(Point gravityCenter, Rect PMIRect, Rect eyeRect) {
        double x = gravityCenter.x + PMIRect.x + eyeRect.x;
        double y = gravityCenter.y + PMIRect.y + eyeRect.y;
        return new Point(x, y);
    }
}
