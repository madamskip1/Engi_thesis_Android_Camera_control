package org.pw.engithesis.androidcameracontrol;

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
    private final EyeAspectRatio earCalculator;
    public double leftEyeEAR;
    public double rightEyeEAR;

    public EyeBlinkDetector() {
        earCalculator = new EyeAspectRatio();
    }

    public void checkEyeBlink(Point[] rightEyeLandmarks, Point[] leftEyeLandmarks) {
        leftEyeEAR = earCalculator.calcEAR(leftEyeLandmarks);
        rightEyeEAR = earCalculator.calcEAR(rightEyeLandmarks);

        notifyUpdate();
    }
}
