package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.MatOfPoint2f;

public class EyeAspectRatio {

    public double getEAR(MatOfPoint2f eye) {
        double distVerticalA = getDistance(eye.get(1, 0), eye.get(5, 0));
        double distVerticalB = getDistance(eye.get(2, 0), eye.get(4, 0));
        double distHorizontal = getDistance(eye.get(0, 0), eye.get(3, 0));

        double EAR = (distVerticalA + distVerticalB) / (2.0 * distHorizontal);

        return EAR;
    }

    private double getDistance(double[] pointA, double[] pointB) {
        double distX = Math.abs(pointA[0] - pointB[0]);
        double distY = Math.abs(pointA[1] - pointB[1]);

        return Math.hypot(distX, distY);
    }
}
