package org.pw.engithesis.androidcameracontrol.eyedetectionalgorithms;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.Arrays;

public class EyeDetectionFacemarks {
    public static double ADD_TOP = 0.7;
    public static double ADD_BOTTOM = 0.5;
    public static double ADD_LEFT = 0.2;
    public static double ADD_RIGHT = 0.2;


    public Rect[] detect(Point[] rightEyeFacemarks, Point[] leftEyeFacemarks) {
        Rect[] eyes = new Rect[2];
        eyes[0] = detectEye(rightEyeFacemarks);
        eyes[1] = detectEye(leftEyeFacemarks);

        return eyes;
    }

    private Rect detectEye(Point[] facemarks) {
        int x1 = (int) facemarks[0].x;
        int x2 = (int) facemarks[3].x;
        double y1 = Arrays.stream(new double[]{facemarks[0].y, facemarks[1].y, facemarks[2].y, facemarks[3].y}).min().getAsDouble();
        double y2 = Arrays.stream(new double[]{facemarks[0].y, facemarks[3].y, facemarks[4].y, facemarks[5].y}).max().getAsDouble();
        int width = x2 - x1;
        int height = (int) (y2 - y1);

        Rect eye = new Rect(x1, (int) y1, width, height);

        return addSpaceToRect(eye, width, height);
    }

    private Rect addSpaceToRect(Rect eye, int width, int height) {
        int addTop = (int) (height * ADD_TOP);
        int addRight = (int) (width * ADD_RIGHT);
        int addBottom = (int) (height * ADD_BOTTOM);
        int addLeft = (int) (width * ADD_LEFT);

        eye.x -= addLeft;
        eye.y -= addTop;
        eye.width += addLeft + addRight;
        eye.height += addTop + addBottom;

        return eye;
    }
}
