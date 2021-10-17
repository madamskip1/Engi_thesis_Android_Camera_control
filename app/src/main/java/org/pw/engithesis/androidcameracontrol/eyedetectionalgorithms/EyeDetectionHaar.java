package org.pw.engithesis.androidcameracontrol.eyedetectionalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.RawResourceManager;

public class EyeDetectionHaar implements EyeDetectionAlgorithm {
    private final CascadeClassifier classifier;

    public EyeDetectionHaar() {
        RawResourceManager haarModel = new RawResourceManager(R.raw.haarcascade_eye, "haarcascade_eye");
        classifier = new CascadeClassifier(haarModel.getPath());
    }

    @Override
    public Rect[] detect(Mat eyesRegionMat) {
        MatOfRect detectedEyes = new MatOfRect();
        classifier.detectMultiScale(eyesRegionMat, detectedEyes);

        return detectedEyes.toArray();
    }
}
