package org.pw.engithesis.androidcameracontrol;

import android.util.Log;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.pw.engithesis.androidcameracontrol.interfaces.Observable;

/*
 *
 * TODO :
 *  Predykcja czy bylo mrugniecie
 *  Mrugniecie oka musi trwać X klatek, żeby zostało zaliczone
 *
 *
 */

public class EyeBlinkDetector extends Observable {
    public static final int LEFT_EYE = 0;
    public static final int RIGHT_EYE = 1;

    public static double BLINK_THRESHOLD = 0.3;

    public double leftEyeEAR;
    public double rightEyeEAR;

    private EyeAspectRatio earCalculator;

    public EyeBlinkDetector() {
        earCalculator = new EyeAspectRatio();
    }

    public void checkEyeBlink(MatOfPoint2f faceLandmarks) {
        Point[] leftEye = getLeftEye(faceLandmarks);
        Point[] rightEye = getRightEye(faceLandmarks);

        leftEyeEAR = earCalculator.calcEAR(leftEye);
        rightEyeEAR = earCalculator.calcEAR(rightEye);
        Log.e("fm", "ear: " + this.leftEyeEAR + ", " + this.rightEyeEAR);

       // TODO: Predykcja czy bylo mrugniecie

        notifyUpdate();
    }

    private Point[] getLeftEye(MatOfPoint2f faceLandmarks) {
        return getEye(faceLandmarks, LEFT_EYE);
    }

    private Point[] getRightEye(MatOfPoint2f faceLandmarks) {
        return getEye(faceLandmarks, RIGHT_EYE);
    }

    private Point[] getEye(MatOfPoint2f faceLandmarks, int index) {
        Point[] eye = new Point[6];
        double[] temp;
        int moddifier = (index == RIGHT_EYE ? 6 : 0);

        temp = faceLandmarks.get(36 + moddifier, 0);
        eye[0] = new Point(temp[0], temp[1]);
        temp = faceLandmarks.get(37 + moddifier, 0);
        eye[1] = new Point(temp[0], temp[1]);
        temp = faceLandmarks.get(38 + moddifier, 0);
        eye[2] = new Point(temp[0], temp[1]);
        temp = faceLandmarks.get(39 + moddifier, 0);
        eye[3] = new Point(temp[0], temp[1]);
        temp = faceLandmarks.get(40 + moddifier, 0);
        eye[4] = new Point(temp[0], temp[1]);
        temp = faceLandmarks.get(41 + moddifier, 0);
        eye[5] = new Point(temp[0], temp[1]);

        return eye;
    }
}
