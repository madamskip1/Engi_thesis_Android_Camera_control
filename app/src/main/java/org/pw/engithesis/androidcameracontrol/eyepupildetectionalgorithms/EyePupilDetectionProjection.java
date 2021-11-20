package org.pw.engithesis.androidcameracontrol.eyepupildetectionalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.Utility;

public class EyePupilDetectionProjection extends EyePupilDetectionAlgorithm {
    private final ProjectionFunctions projectionFunction = ProjectionFunctions.General;
    private double generalProjectionParameter;

    @Override
    public Point detect(Mat frame, Rect eyeRegion) {
        Mat eyeMat = frame.submat(eyeRegion);

        ProjectionStruct projection = calcProjection(eyeMat);

        return null;
    }

    private ProjectionStruct calcProjection(Mat eyeMat) {
        int width = eyeMat.width();
        int height = eyeMat.height();

        ProjectionStruct projection = null;

        byte[] pixels = Utility.matToByteArray(eyeMat);
        ProjectionStruct IPF = calcIPF(pixels, width, height);

        if (projectionFunction == ProjectionFunctions.Integral) {
            projection = IPF;
        } else {
            ProjectionStruct VPF = calcVPF(pixels, width, height, IPF);

            if (projectionFunction == ProjectionFunctions.Variance) {
                projection = VPF;
            } else if (projectionFunction == ProjectionFunctions.General) {
                projection = calcGPF(width, height, IPF, VPF);
            }
        }

        return projection;
    }

    private ProjectionStruct calcIPF(byte[] pixels, int width, int height) {
        int[] verticalProjection = new int[width];
        int[] horizontalProjection = new int[height];

        for (int x = 0; x < width; x++) {
            verticalProjection[x] = calcIPFValue(pixels, x, width, height);
        }

        for (int y = 0; y < height; y++) {
            horizontalProjection[y] = calcIPFValue(pixels, y * width, 1, width);
        }

        return new ProjectionStruct(verticalProjection, horizontalProjection);
    }

    private ProjectionStruct calcVPF(byte[] pixels, int width, int height, ProjectionStruct IPF) {
        int[] verticalProjection = new int[width];
        int[] horizontalProjection = new int[height];

        for (int x = 0; x < width; x++) {
            verticalProjection[x] = calcVPFValue(pixels, IPF.vertical[x], x, width, height);
        }
        for (int y = 0; y < height; y++) {
            horizontalProjection[y] = calcVPFValue(pixels, IPF.horizontal[y], y * width, 1, width);
        }

        return new ProjectionStruct(verticalProjection, horizontalProjection);
    }

    private ProjectionStruct calcGPF(int width, int height, ProjectionStruct IPF, ProjectionStruct VPF) {
        int[] verticalProjection = new int[width];
        int[] horizontalProjection = new int[height];

        for (int x = 0; x < width; x++) {
            verticalProjection[x] = calcGPFValue(IPF.vertical[x], VPF.vertical[x]);
        }
        for (int y = 0; y < width; y++) {
            horizontalProjection[y] = calcGPFValue(IPF.vertical[y], VPF.vertical[y]);
        }

        return new ProjectionStruct(verticalProjection, horizontalProjection);
    }

    private int calcIPFValue(byte[] pixels, int startIndex, int step, int length) {
        int sumColIntensity = 0;
        int index = startIndex;

        for (int i = 0; i < length; i++) {
            sumColIntensity += pixels[index];
            index += step;
        }

        return sumColIntensity / length;
    }

    private int calcVPFValue(byte[] pixels, int IPF, int startIndex, int step, int length) {
        int sumColIntensity = 0;
        int index = startIndex;

        for (int i = 0; i < length; i++) {
            int vpf = pixels[index] - IPF;
            vpf = vpf * vpf;

            sumColIntensity += vpf;
            index += step;
        }

        return sumColIntensity / length;
    }

    private int calcGPFValue(int IPF, int VPF) {
        // (1 - alfa) * IPF(y) + alfa * VPF(y)
        return (int) ((1 - generalProjectionParameter) * IPF + generalProjectionParameter * VPF);
    }


    public enum ProjectionFunctions {
        Integral, // IPF - Integral Projection Function
        Variance, // VPF - Variance Projection Function
        General   // GPF - General Projection Function
    }

    private class ProjectionStruct {
        public final int[] vertical;
        public final int[] horizontal;


        public ProjectionStruct(int[] vertical, int[] horizontal) {
            this.vertical = vertical;
            this.horizontal = horizontal;
        }
    }
}
