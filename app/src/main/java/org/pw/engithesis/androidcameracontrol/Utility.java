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

    public static void drawRects(Mat mat, Rect[] rects, Scalar color) {
        for (Rect rect : rects) {
            if (rect != null) {
                Imgproc.rectangle(mat, rect.tl(), rect.br(), color, 3);
            }
        }
    }

    public static void drawRects(Mat mat, Rect[] rects) {
        drawRects(mat, rects, new Scalar(255, 0, 0));
    }

    public static void drawVerticalLine(Mat mat, int x) {
        Imgproc.line(mat, new Point(x, 0), new Point(x, mat.height()), new Scalar(255, 0, 0), 3);
    }

}
