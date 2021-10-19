package org.pw.engithesis.androidcameracontrol.thresholding;

public class IntensityThresholding extends ImageThresholding {
    private double intensity;

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    @Override
    protected byte filterFunction(byte pixel) {
        int pixelValue = pixel & 0xFF;

        if (pixelValue < intensity) {
            return (byte) 255;
        }

        return 0;
    }
}
