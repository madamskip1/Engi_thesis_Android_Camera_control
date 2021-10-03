package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.pw.engithesis.androidcameracontrol.interfaces.ImageThresholding;
import org.pw.engithesis.androidcameracontrol.interfaces.Observable;

public class EyePupilDetector extends Observable {
    public Point[] pupils;

    private byte[] grayEyePixels;

    public void detectPupils(Mat face, Rect[] eyesRects) {
        pupils = null;
        int eyesNum = eyesRects.length;
        pupils = new Point[eyesNum];

        for (int i = 0; i < eyesNum; i++) {
            if (eyesRects[i] == null) {
                pupils[i] = null;
            } else {
                pupils[i] = detect(face, eyesRects[i]);
            }
        }

        notifyUpdate();
    }

    private Point detect(Mat face, Rect eyeRect) {
        Mat eye = face.submat(eyeRect);
        Mat grayEye = new Mat();
        Imgproc.cvtColor(eye, grayEye, Imgproc.COLOR_RGB2GRAY);

        grayEyePixels = matToByteArray(grayEye);

        CDFLuminanceThresholding cdf = new CDFLuminanceThresholding();
        Mat binaryEye = cdf.thresholdNewMat(grayEye);

        Mat erodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(2, 2));
        Mat erodeMat = new Mat();
        Imgproc.erode(binaryEye, erodeMat, erodeKernel);

        Point darkestPixel = getDarkestPixel(erodeMat);
        double avgIntensity = calcAvgIntensity(darkestPixel, eye.width(), eye.height());

        Rect PMIRect = getPMIRect((int) darkestPixel.x, (int) darkestPixel.y, eye.width(), eye.height());
        Mat PMIMat = grayEye.submat(PMIRect);

        Mat erodedPMI = new Mat();
        Imgproc.erode(PMIMat, erodedPMI, erodeKernel);

        ImageThresholding imageThresholding = prepareThresholdFunction(avgIntensity);
        imageThresholding.thresholdRef(PMIMat);

        Point eyePupil = calcGravityCenter(PMIMat);
        eyePupil.x += PMIRect.x + eyeRect.x;
        eyePupil.y += PMIRect.y + eyeRect.y;

        return eyePupil;
    }

    private Point getDarkestPixel(Mat erodedMat) {
        int length = (int) erodedMat.total();
        byte[] erodedPixels = matToByteArray(erodedMat);

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

        return indexToPoint(darkestPixelIndex, erodedMat.width());
    }

    private Point indexToPoint(int i, int width) {
        int y = i / width;
        int x = i - (y * width);

        return new Point(x, y);
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
        byte[] pixels = matToByteArray(mat);

        int rowSum = 0;
        int colSum = 0;
        int whitePixelCount = 0;

        for (int i = 0; i < pixelNum; i++) {
            if (pixels[i] == -1) {
                Point point = indexToPoint(i, width);
                rowSum += point.y;
                colSum += point.x;
                whitePixelCount++;
            }
        }

        return new Point(rowSum / whitePixelCount, colSum / whitePixelCount);
    }

    private byte[] matToByteArray(Mat mat) {
        byte[] temp = new byte[(int) mat.total() * mat.channels()];
        mat.get(0, 0, temp);
        return temp;
    }

    private ImageThresholding prepareThresholdFunction(double avgIntensity) {
        return new ImageThresholding() {
            @Override
            protected byte filterFunction(byte pixel) {
                int pixelValue = pixel & 0xFF;

                if (pixelValue < avgIntensity) {
                    return (byte) 255;
                }

                return 0;
            }
        };
    }
}
