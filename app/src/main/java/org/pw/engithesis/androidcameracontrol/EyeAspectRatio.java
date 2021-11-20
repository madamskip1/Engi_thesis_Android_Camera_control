package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Point;

public class EyeAspectRatio {

    public double calcEAR(Point[] eyeFacemarks) {
        double distVerticalA = Utility.calcDistance(eyeFacemarks[1], eyeFacemarks[5]);
        double distVerticalB = Utility.calcDistance(eyeFacemarks[2], eyeFacemarks[4]);
        double distHorizontal = Utility.calcDistance(eyeFacemarks[0], eyeFacemarks[3]);

        return (distVerticalA + distVerticalB) / (2.0 * distHorizontal);
    }
}
