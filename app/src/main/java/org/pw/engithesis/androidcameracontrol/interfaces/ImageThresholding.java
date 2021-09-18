package org.pw.engithesis.androidcameracontrol.interfaces;

import org.opencv.core.Mat;

public abstract class ImageThresholding {
    protected Mat matToThreshold;

    public void threhsoldRef(Mat mat) {
        matToThreshold = mat;
        threshold();
    }

    public Mat thresholdNewMat(Mat mat) {
        matToThreshold = mat.clone();
        threshold();
        return matToThreshold;
    }

    private void threshold() {
        beforeThresholding();

        int numPixels = (int) matToThreshold.total();
        byte[] pixels = new byte[numPixels];

        matToThreshold.get(0, 0, pixels);
        for (int i = 0; i < numPixels; i++) {
            pixels[i] = filterFunction(pixels[i]);
        }
        matToThreshold.put(0, 0, pixels);

    }

    protected int byteToInt(byte b) {
        return b & 0xFF;
    }

    protected byte intToByte(int i) {
        return (byte) i;
    }

    protected void beforeThresholding() {
    }

    protected abstract byte filterFunction(byte pixel);
}
