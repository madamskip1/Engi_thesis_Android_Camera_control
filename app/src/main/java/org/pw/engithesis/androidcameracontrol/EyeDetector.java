package org.pw.engithesis.androidcameracontrol;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

public class EyeDetector {
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

        int newX = face.x + (int) (face.width * cropLeft);
        int newY = face.y + (int) (face.height * cropTop);
        int newWidth = (int) (face.width * (1.0 - cropRight - cropLeft));
        int newHeight = (int) (face.height * (1.0 - cropBottom - cropTop));

        Rect eyesRegion = new Rect(newX, newY, newWidth, newHeight);
        Mat croppedFace = mat.submat(eyesRegion);

        MatOfRect eyes = new MatOfRect();
        classifier.detectMultiScale(croppedFace, eyes);

        Rect[] eyesArray = eyes.toArray();
        int eyesSize = eyesArray.length;

        for (int i = 0; i < eyesSize; i++) {
            eyesArray[i].y += newY;
            eyesArray[i].x += newX;
        }

        return new MatOfRect(eyesArray);
    }
}
