package org.pw.engithesis.androidcameracontrol.eyepupildetectionalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.Utility;

public class EyePupilDetectionProjection extends EyePupilDetectionAlgorithm {
    private static final double generalProjectionParameter = 0.6;
    private static final double MIN_SPACE_BETWEEN_EXTREMA = 0.10;

    private final ProjectionFunctions projectionFunction = ProjectionFunctions.General;
    private byte[] pixels;
    private int width;
    private int height;

    @Override
    public Point detect(Mat frame, Rect eyeRegion) {
        Mat grayEyeMat = getGrayEyeMat(frame, eyeRegion);
        pixels = Utility.matToByteArray(grayEyeMat);
        width = grayEyeMat.width();
        height = grayEyeMat.height();

        ProjectionStruct projection = calcProjection();

        double[] deriveVertical = calcDerivatives(projection.vertical, projection.vertical.length);
        double[] deriveHorizontal = calcDerivatives(projection.horizontal, projection.horizontal.length);

        int[] verticalExtrema = getExtrema(deriveVertical);
        int[] horizontalExtrema = getExtrema(deriveHorizontal);

        Point pupil = calcCenter(verticalExtrema, horizontalExtrema);
        return calcPointInFrame(pupil, eyeRegion);
    }


    private Point calcCenter(int[] verticalExtrema, int[] horizontalExtrema) {
        int x1 = verticalExtrema[0];
        int y1 = horizontalExtrema[0];
        int x2 = verticalExtrema[1];
        int y2 = horizontalExtrema[1];

        return new Point((int) ((x1 + x2) / 2.0), (int) ((y1 + y2) / 2.0));
    }

    private ProjectionStruct calcProjection() {
        ProjectionStruct IPF = calcIPF();
        if (projectionFunction == ProjectionFunctions.Integral) {
            return IPF;
        }

        ProjectionStruct VPF = calcVPF(IPF);
        if (projectionFunction == ProjectionFunctions.Variance) {
            return VPF;
        }

        return calcGPF(IPF, VPF);  // General Projection Function is default
    }

    private ProjectionStruct calcIPF() {
        double[] verticalProjection = new double[width];
        double[] horizontalProjection = new double[height];

        for (int x = 0; x < width; x++) {
            verticalProjection[x] = calcIPFValue(x, width, height);
        }

        for (int y = 0; y < height; y++) {
            horizontalProjection[y] = calcIPFValue(y * width, 1, width);
        }

        return new ProjectionStruct(verticalProjection, horizontalProjection);
    }

    private ProjectionStruct calcVPF(ProjectionStruct IPF) {
        double[] verticalProjection = new double[width];
        double[] horizontalProjection = new double[height];

        for (int x = 0; x < width; x++) {
            verticalProjection[x] = calcVPFValue(IPF.vertical[x], x, width, height);
        }
        for (int y = 0; y < height; y++) {
            horizontalProjection[y] = calcVPFValue(IPF.horizontal[y], y * width, 1, width);
        }

        return new ProjectionStruct(verticalProjection, horizontalProjection);
    }

    private ProjectionStruct calcGPF(ProjectionStruct IPF, ProjectionStruct VPF) {
        double[] verticalProjection = new double[width];
        double[] horizontalProjection = new double[height];

        for (int x = 0; x < width; x++) {
            verticalProjection[x] = calcGPFValue(IPF.vertical[x], VPF.vertical[x]);
        }
        for (int y = 0; y < height; y++) {
            horizontalProjection[y] = calcGPFValue(IPF.horizontal[y], VPF.horizontal[y]);
        }

        return new ProjectionStruct(verticalProjection, horizontalProjection);
    }

    private double calcIPFValue(int startIndex, int step, int length) {
        double sumColIntensity = 0;
        int index = startIndex;

        for (int i = 0; i < length; i++) {
            sumColIntensity += (double) Utility.byteToInt(pixels[index]);
            index += step;
        }

        return sumColIntensity / (double) length;
    }

    private double calcVPFValue(double IPF, int startIndex, int step, int length) {
        double sumColIntensity = 0;
        int index = startIndex;

        for (int i = 0; i < length; i++) {
            double vpf = (double) Utility.byteToInt(pixels[index]) - IPF;
            vpf = vpf * vpf;

            sumColIntensity += vpf;
            index += step;
        }

        return sumColIntensity / (double) length;
    }

    private double calcGPFValue(double IPF, double VPF) {
        // (1 - alfa) * IPF + alfa * VPF
        return ((1.0 - EyePupilDetectionProjection.generalProjectionParameter) * IPF + EyePupilDetectionProjection.generalProjectionParameter * VPF);
    }

    private double[] calcDerivatives(double[] arr, int length) {
        double[] derive = new double[length];
        derive[0] = 0;
        double previous = arr[0];
        double cur;

        for (int i = 1; i < length; i++) {
            cur = arr[i];
            derive[i] = Math.abs(cur - previous);
            previous = cur;
        }

        return derive;
    }

    private int[] getExtrema(double[] derivatives) {
        double max1 = 0;
        int max1Index = 0;
        int minSpace = (int) (derivatives.length * MIN_SPACE_BETWEEN_EXTREMA);

        for (int i = 0; i < derivatives.length; i++) {
            if (max1 < derivatives[i]) {
                max1 = derivatives[i];
                max1Index = i;
            }
        }

        double max2 = 0;
        int max2Index = 0;

        for (int i = 0; i < derivatives.length; i++) {
            int space = Math.abs(max1Index - i);
            if (max2 < derivatives[i] && space >= minSpace) {
                max2 = derivatives[i];
                max2Index = i;
            }
        }

        return new int[]{max1Index, max2Index};
    }

    public enum ProjectionFunctions {
        Integral, // IPF - Integral Projection Function
        Variance, // VPF - Variance Projection Function
        General   // GPF - General Projection Function. Default option
    }

    private static class ProjectionStruct {
        public final double[] vertical;
        public final double[] horizontal;


        public ProjectionStruct(double[] vertical, double[] horizontal) {
            this.vertical = vertical;
            this.horizontal = horizontal;
        }
    }
}
