package org.pw.engithesis.androidcameracontrol;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.pw.engithesis.androidcameracontrol.interfaces.ImageThresholding;

public class EyePupilDetector {
    private ImageViewsBuilder viewsBuilder;
    byte[] eyePixels;

    public void detect(Mat eye, ImageViewsBuilder viewsBuilder) {
        this.viewsBuilder = viewsBuilder;


        Mat grayEye = new Mat();
        Imgproc.cvtColor(eye, grayEye, Imgproc.COLOR_RGB2GRAY);
        addBM(grayEye);
        eyePixels = new byte[(int) grayEye.total()];
        grayEye.get(0, 0, eyePixels);

        CDFLuminanceThresholding cdf = new CDFLuminanceThresholding();
        Mat thresholdEye = cdf.thresholdNewMat(grayEye);

        Mat erodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(2, 2));
        Mat erodeMat = new Mat();
        Imgproc.erode(thresholdEye, erodeMat, erodeKernel);
        addBM(erodeMat);

        Point darkestPixel = getDarkestPixel(erodeMat);
        double avgIntensity = calcAvgIntensity(darkestPixel, eye.width(), eye.height());

        Rect PMIRect = getPMIRect((int) darkestPixel.x, (int) darkestPixel.y, eye.width(), eye.height());
        Mat PMIMat = grayEye.submat(PMIRect);

        Mat erodedPMI = new Mat();
        Imgproc.erode(PMIMat, erodedPMI, erodeKernel);
        addBM(erodedPMI);

        ImageThresholding imageThresholding = new ImageThresholding() {
            @Override
            protected byte filterFunction(byte pixel) {
                int pixelValue = pixel & 0xFF;

                if (pixelValue < avgIntensity) {
                    return (byte) 255;
                }

                return 0;
            }
        };

        Mat ThresholdedPMI = imageThresholding.thresholdNewMat(PMIMat);

        Point pupil = calcGravityCenter(ThresholdedPMI);
        pupil.x += PMIRect.x;
        pupil.y += PMIRect.y;
        Mat eyeWithPupil = eye.clone();
        Imgproc.circle(eyeWithPupil, pupil, 2, new Scalar(255, 200, 0), 1);
        addBM(eyeWithPupil);
    }

    private void addBM(Mat mat) {
        Bitmap tempBM = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, tempBM);
        viewsBuilder.addImage(tempBM);
    }

    private Point getDarkestPixel(Mat erodedMat) {
        int length = (int) erodedMat.total();
        byte[] erodedPixels = new byte[length];
        erodedMat.get(0, 0, erodedPixels);

        int darkestPixelIndex = 0;
        int darkestPixelLuminance = 255;

        for (int i = 0; i < length; i++) {
            if (erodedPixels[i] == (byte) 255) // is white on eroded mat
            {
                int value = eyePixels[i] & 0xFF;
                if (value < darkestPixelLuminance) {
                    darkestPixelLuminance = value;
                    darkestPixelIndex = i;
                }
            }
        }

        Log.e("daa", "index" + darkestPixelIndex + " width" + erodedMat.width());
        return indexToPoint(darkestPixelIndex, erodedMat.width());
    }

    private Point indexToPoint(int i, int width) {
        int y = i / width;
        int x = i - (y * width);

        return new Point(x, y);
    }

    private double calcAvgIntensity(Point anchor, int width, int height) {
        int top = (int) anchor.y - 4;
        top = (top >= 0 ? top : 0);
        int bottom = (int) anchor.y + 5;
        bottom = (bottom < height ? bottom : height - 1);
        int left = (int) anchor.x - 4;
        left = (left >= 0 ? left : 0);
        int right = (int) anchor.x + 5;
        right = (right < width ? right : width - 1);

        double avgIntensity = 0.0;

        for (int y = top; y <= bottom; y++) {
            for (int x = left; x <= right; x++) {
                int index = y * width + x;
                int pixelValue = eyePixels[index] & 0xFF;
                avgIntensity += pixelValue;
            }
        }

        avgIntensity /= ((bottom - top) * (right - left));

        return avgIntensity;
    }

    private Rect getPMIRect(int x, int y, int width, int height) {
        int rectLeft = x - 7;
        rectLeft = (rectLeft >= 0 ? rectLeft : 0);

        int rectTop = y - 7;
        rectTop = (rectTop >= 0 ? rectTop : 0);

        int rectRight = x + 7;
        rectRight = (rectRight < width ? rectRight : width - 1);

        int rectBottom = y + 7;
        rectBottom = (rectBottom < height ? rectBottom : height - 1);

        int rectWidth = rectRight - rectLeft;
        int rectHeight = rectBottom - rectTop;

        return new Rect(rectLeft, rectTop, rectWidth, rectHeight);
    }

    private Point calcGravityCenter(Mat mat) {
        int pixelNum = (int) mat.total();
        int width = mat.width();
        byte[] pixels = new byte[pixelNum];
        mat.get(0, 0, pixels);
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

}
