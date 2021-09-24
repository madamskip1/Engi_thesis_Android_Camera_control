package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;

import org.opencv.objdetect.CascadeClassifier;

public class EyeDetector {
    public static final int RIGHT_EYE_INDEX = 0;
    public static final int LEFT_EYE_INDEX = 1;

    private final CascadeClassifier classifier;
    private static final double cropTop = 0.1;
    private static final double cropBottom = 0.45;
    private static final double cropLeft = 0.1;
    private static final double cropRight = 0.1;

    public EyeDetector() {
        RawResourceManager haarModel = new RawResourceManager(R.raw.haarcascade_eye, "haarcascade_eye");
        classifier = new CascadeClassifier(haarModel.getPath());
    }

    public MatOfRect detect(Mat mat, Rect face) {
        Rect eyesRegion = getEyesRegionRect(face);
        Mat croppedFace = mat.submat(eyesRegion);

        MatOfRect eyes = new MatOfRect();
        classifier.detectMultiScale(croppedFace, eyes);
        Rect[] orderedEyesArray = sortEyes(face, eyesRegion, eyes.toArray());

        return new MatOfRect(orderedEyesArray);
    }

    private Rect getEyesRegionRect(Rect face) {
        int eyesRegionX = face.x + (int) (face.width * cropLeft);
        int eyesRegionY = face.y + (int) (face.height * cropTop);
        int eyesRegionWidth = (int) (face.width * (1.0 - cropRight - cropLeft));
        int eyesRegionHeight = (int) (face.height * (1.0 - cropBottom - cropTop));

        return new Rect(eyesRegionX, eyesRegionY, eyesRegionWidth, eyesRegionHeight);
    }


    private int predictEyeSide(Rect face, Rect eye) {
        int faceCenterX = getCenterX(face);
        int eyeCenterX = getCenterX(eye);

        if (eyeCenterX < faceCenterX) {
            return RIGHT_EYE_INDEX;
        } else {
            return LEFT_EYE_INDEX;
        }
    }

    private int getCenterX(Rect rect) {
        return rect.x + (rect.width / 2);
    }

    private Rect[] sortEyes(Rect face, Rect eyesRegion, Rect[] eyes) {
        int eyesSize = eyes.length;

        if (eyesSize == 0) {
            return new Rect[]{null, null};
        }

        for (Rect rect : eyes) {
            rect.y += eyesRegion.y;
            rect.x += eyesRegion.x;
        }

        if (eyesSize > 2) {
            return eyes;
        }

        Rect[] orderedEyesArray = new Rect[2];

        int eyeSide = predictEyeSide(face, eyes[0]);
        orderedEyesArray[eyeSide] = eyes[0];
        orderedEyesArray[eyeSide == 1 ? 0 : 1] = (eyesSize == 2 ? eyes[1] : null);

        return orderedEyesArray;
    }
}
