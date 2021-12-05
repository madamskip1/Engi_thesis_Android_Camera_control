package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Point;

public class EARClosedEyeDetector {
    public static double CLOSED_THRESHOLD = 0.19;
    private final EyeAspectRatio earCalculator;

    EARClosedEyeDetector() {
        earCalculator = new EyeAspectRatio();
    }

    public boolean areClosed(Point[] rightEyeFacemarks, Point[] leftEyeFacemarks) {
        double leftEyeEAR = earCalculator.calcEAR(leftEyeFacemarks);
        double rightEyeEAR = earCalculator.calcEAR(rightEyeFacemarks);

        return checkIfClosed(leftEyeEAR) || checkIfClosed(rightEyeEAR);
    }

    private boolean checkIfClosed(double EAR) {
        return EAR <= CLOSED_THRESHOLD;
    }
}
