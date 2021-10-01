package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

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

    public static void drawRects(Mat mat, Rect[] rects) {
        for (Rect rect : rects) {
            if (rect != null) {
                Imgproc.rectangle(mat, rect.tl(), rect.br(), new Scalar(255, 0, 0), 3);
            }
        }
    }

}