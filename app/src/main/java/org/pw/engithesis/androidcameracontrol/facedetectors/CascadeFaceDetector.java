package org.pw.engithesis.androidcameracontrol.facedetectors;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;
import org.pw.engithesis.androidcameracontrol.RawResourceManager;

public class CascadeFaceDetector extends FaceDetector {
    int resourceID = -1;
    String resourceName;
    private CascadeClassifier classifier;

    public CascadeFaceDetector(int resID, String resName) {
        resourceID = resID;
        resourceName = resName;
        loadClassifier();
    }

    @Override
    public Rect detect(Mat mat) {
        MatOfRect matOfRect = new MatOfRect();
        classifier.detectMultiScale(mat, matOfRect);

        Rect[] faces = matOfRect.toArray();
        return filterFaces(mat, faces);
    }

    private void loadClassifier() {
        RawResourceManager classifierResource = new RawResourceManager(resourceID, resourceName);

        classifier = new CascadeClassifier(classifierResource.getPath());
    }
}
