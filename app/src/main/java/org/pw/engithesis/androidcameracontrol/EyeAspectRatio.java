package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Point;

public class EyeAspectRatio {

    public double calcEAR(Point[] eye) {
        double distVerticalA = Utility.calcDistance(eye[1], eye[5]);
        double distVerticalB = Utility.calcDistance(eye[2], eye[4]);
        double distHorizontal = Utility.calcDistance(eye[0], eye[3]);

        double EAR = (distVerticalA + distVerticalB) / (2.0 * distHorizontal);

        return EAR;
    }
}
