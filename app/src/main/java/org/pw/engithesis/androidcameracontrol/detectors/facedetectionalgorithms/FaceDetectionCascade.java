package org.pw.engithesis.androidcameracontrol.detectors.facedetectionalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;
import org.pw.engithesis.androidcameracontrol.RawResourceManager;

public class FaceDetectionCascade implements FaceDetectionAlgorithm {
    int resourceID = -1;
    String resourceName;
    private CascadeClassifier classifier;

    public FaceDetectionCascade(int resID, String resName) {
        resourceID = resID;
        resourceName = resName;
        loadClassifier();
    }

    public Rect[] detect(Mat frame) {
        MatOfRect matOfRect = new MatOfRect();
        classifier.detectMultiScale(frame, matOfRect);

        return matOfRect.toArray();
    }

    private void loadClassifier() {
        RawResourceManager classifierResource = new RawResourceManager(resourceID, resourceName);

        classifier = new CascadeClassifier(classifierResource.getPath());
    }
}
