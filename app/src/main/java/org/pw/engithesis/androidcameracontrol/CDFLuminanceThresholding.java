package org.pw.engithesis.androidcameracontrol;

import org.pw.engithesis.androidcameracontrol.interfaces.ImageThresholding;

import java.util.Arrays;

public class CDFLuminanceThresholding extends ImageThresholding {
    private static final double DEFAULT_THRESHOLD_PARAMETER = 0.05;
    private final double thresholdParameter;
    private double[] cumulativeDistributionOfLuminance;
    private double[] counterPixelsLuminance;

    public CDFLuminanceThresholding() {
        thresholdParameter = DEFAULT_THRESHOLD_PARAMETER;
        initArrays();
    }

    public CDFLuminanceThresholding(double threshold) {
        thresholdParameter = threshold;
        initArrays();
    }


    private void initArrays() {
        cumulativeDistributionOfLuminance = new double[256];
        Arrays.fill(cumulativeDistributionOfLuminance, 1.0);
        counterPixelsLuminance = new double[256];
    }

    @Override
    protected byte filterFunction(byte pixel) {
        if (getCDF(pixel) <= thresholdParameter) {
            return intToByte(255);
        }

        return intToByte(0);
    }

    private double getCDF(byte pixel) {
        int luminance = byteToInt(pixel);
        return cumulativeDistributionOfLuminance[luminance];
    }

    @Override
    protected void beforeThresholding() {
        countPixelsLuminance();
        calcCDF();
    }

    private void countPixelsLuminance() {
        int numPixels = (int) matToThreshold.total();
        byte[] pixels = new byte[numPixels];
        matToThreshold.get(0, 0, pixels);

        for (int i = 0; i < numPixels; i++) {
            int luminance = byteToInt(pixels[i]);
            counterPixelsLuminance[luminance]++;
        }
    }

    private void calcCDF() {
        int numPixels = (int) matToThreshold.total();
        cumulativeDistributionOfLuminance[0] = counterPixelsLuminance[0] / numPixels;

        for (int i = 1; i < 256; i++) {
            double probability = cumulativeDistributionOfLuminance[i - 1];
            probability += counterPixelsLuminance[i] / numPixels;

            cumulativeDistributionOfLuminance[i] = probability;

            if (probability > thresholdParameter) {
                i = 256;
            }
        }
    }
}
