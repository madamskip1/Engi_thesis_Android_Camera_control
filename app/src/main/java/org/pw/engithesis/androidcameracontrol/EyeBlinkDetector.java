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

    public static double BLINK_THRESHOLD = 0.19;
    private final EyeAspectRatio earCalculator;
    public double leftEyeEAR;
    public double rightEyeEAR;

    public EyeBlinkDetector() {
        earCalculator = new EyeAspectRatio();
    }

    public void checkEyeBlink(Point[] rightEyeFacemarks, Point[] leftEyeFacemarks) {
        leftEyeEAR = earCalculator.calcEAR(leftEyeFacemarks);
        rightEyeEAR = earCalculator.calcEAR(rightEyeFacemarks);

        notifyUpdate();
    }

    public boolean isLeftBlink() {
        return leftEyeEAR <= BLINK_THRESHOLD;
    }

    public boolean isRightBlink() {
        return rightEyeEAR <= BLINK_THRESHOLD;
    }

}
