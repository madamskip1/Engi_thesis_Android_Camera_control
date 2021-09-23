package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Point;

public class Utility {
    private Utility() {
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public static double calcDistance(Point pointA, Point pointB) {
        double distX = Math.abs(pointA.x - pointB.x);
        double distY = Math.abs(pointA.y - pointB.y);

        return Math.hypot(distX, distY);
    }
}
