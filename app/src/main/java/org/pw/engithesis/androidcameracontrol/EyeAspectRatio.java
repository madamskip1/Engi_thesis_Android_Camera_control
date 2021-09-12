package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Point;

public class EyeAspectRatio {

    public double calcEAR(Point[] eye) {
        double distVerticalA = getDistance(eye[1], eye[5]);
        double distVerticalB = getDistance(eye[2], eye[4]);
        double distHorizontal = getDistance(eye[0], eye[3]);

        double EAR = (distVerticalA + distVerticalB) / (2.0 * distHorizontal);

        return EAR;
    }

    private double getDistance(Point pointA, Point pointB) {
        double distX = Math.abs(pointA.x - pointB.y);
        double distY = Math.abs(pointA.x - pointB.y);

        return Math.hypot(distX, distY);
    }
}
