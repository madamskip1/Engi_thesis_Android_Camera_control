package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.ArrayList;

public class EyeFilter {

    public Rect[] filter(Rect[] eyes, Rect eyesRegion) {
        ArrayList<Rect> leftEyeGroup = new ArrayList<>();
        ArrayList<Rect> rightEyeGroup = new ArrayList<>();

        Point eyesRegionCenter = Utility.getCenterOfRect(eyesRegion);

        for (Rect eye : eyes) {
            Point eyeCenter = Utility.getCenterOfRect(eye);
            eyeCenter.x += eyesRegion.x;

            if (eyeCenter.x <= eyesRegionCenter.x) {
                rightEyeGroup.add(eye);
            } else {
                leftEyeGroup.add(eye);
            }
        }

        Rect rightEye = filterEyeGroup(rightEyeGroup);
        Rect leftEye = filterEyeGroup(leftEyeGroup);

        return new Rect[]{rightEye, leftEye};
    }

    private Rect filterEyeGroup(ArrayList<Rect> eyeGroup) {
        if (eyeGroup.isEmpty()) {
            return null;
        }

        return getBiggestEyeRect(eyeGroup);
    }

    private Rect getBiggestEyeRect(ArrayList<Rect> eyes) {
        Rect biggestRect = eyes.get(0);
        double biggestRectSize = biggestRect.width * biggestRect.height;

        for (int i = 1; i < eyes.size(); i++) {
            Rect curRect = eyes.get(i);
            double curSize = curRect.width * curRect.height;

            if (curSize > biggestRectSize) {
                biggestRect = curRect;
                biggestRectSize = curSize;
            }
        }

        return biggestRect;
    }
}
