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

    public static void drawRect(Mat mat, Rect rect, Scalar color, int thickness) {
        Imgproc.rectangle(mat, rect.tl(), rect.br(), color, thickness);
    }

    public static void drawRects(Mat mat, Rect[] rects, Scalar color) {
        for (Rect rect : rects) {
            if (rect != null) {
                drawRect(mat, rect, color, 1);
            }
        }
    }

    public static void drawRects(Mat mat, Rect[] rects) {
        drawRects(mat, rects, new Scalar(255, 0, 0));
    }

    public static void drawCircle(Mat mat, Point point) {
        Imgproc.circle(mat, point, 1, new Scalar(200, 200, 200), 40);
    }

    public static void drawVerticalLine(Mat mat, int x) {
        Imgproc.line(mat, new Point(x, 0), new Point(x, mat.height()), new Scalar(255, 0, 0), 1);
    }

    public static void drawHorizontalLine(Mat mat, int y) {
        Imgproc.line(mat, new Point(0, y), new Point(mat.width(), y), new Scalar(255, 0, 0), 1);
    }

    public static Point getCenterOfRect(Rect rect) {
        int centerX = rect.x + (rect.width / 2);
        int centerY = rect.y + (rect.height / 2);

        return new Point(centerX, centerY);
    }

    public static byte[] matToByteArray(Mat mat) {
        byte[] temp = new byte[(int) mat.total() * mat.channels()];
        mat.get(0, 0, temp);
        return temp;
    }

    public static Point indexToPoint(int i, int width) {
        int y = i / width;
        int x = i - (y * width);

        return new Point(x, y);
    }

    public static int byteToInt(byte b) {
        return b & 0xFF;
    }
}
