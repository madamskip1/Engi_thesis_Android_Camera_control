package org.pw.engithesis.androidcameracontrol.eyedetectionalgorithms;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.Arrays;

public class EyeDetectionLandmark {
    private static final double ADD_TOP = 0.0;
    private static final double ADD_BOTTOM = 0.0;
    private static final double ADD_LEFT = 0.0;
    private static final double ADD_RIGHT = 0.0;


    public Rect[] detect(Point[] rightEyeLandmarks, boolean rightEyeBlinking, Point[] leftEyeLandmarks, boolean leftEyeBlinking) {
        Rect[] eyes = new Rect[2];
        eyes[0] = detectEye(rightEyeLandmarks, rightEyeBlinking);
        eyes[1] = detectEye(leftEyeLandmarks, leftEyeBlinking);

        return eyes;
    }

    private Rect detectEye(Point[] landmarks, boolean blinking) {
        if (blinking) {
            return null;
        } else {
            int x1 = (int) landmarks[0].x;
            int x2 = (int) landmarks[3].x;
            double y1 = Arrays.stream(new double[]{landmarks[0].y, landmarks[1].y, landmarks[2].y, landmarks[3].y}).max().getAsDouble();
            double y2 = Arrays.stream(new double[]{landmarks[0].y, landmarks[3].y, landmarks[4].y, landmarks[5].y}).min().getAsDouble();

            int width = x2 - x1;
            int height = (int) (y2 - y1);

            Rect eye = new Rect(x1, (int) y1, width, height);

            return addSpaceToRect(eye, width, height);
        }
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
