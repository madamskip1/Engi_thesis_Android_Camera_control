package org.pw.engithesis.androidcameracontrol.thresholding;

import org.opencv.core.Mat;

public abstract class ImageThresholding {
    protected Mat matToThreshold;

    public Mat thresholdRef(Mat mat) {
        matToThreshold = mat;
        threshold();
        return mat;
    }

    public Mat thresholdNewMat(Mat mat) {
        return thresholdRef(mat.clone());
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

    protected abstract byte filterFunction(byte pixel);

    protected void beforeThresholding() {
    }


    protected int byteToInt(byte b) {
        return b & 0xFF;
    }

    protected byte intToByte(int i) {
        return (byte) i;
    }
}
