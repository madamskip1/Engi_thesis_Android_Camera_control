package org.pw.engithesis.androidcameracontrol.facedetectors;

import android.content.Context;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;
import org.pw.engithesis.androidcameracontrol.App;
import org.pw.engithesis.androidcameracontrol.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CascadeFaceDetector extends FaceDetector {
    private File mCascadeFile;
    private CascadeClassifier classifier;
    int resourceID = -1;
    String resourceName;

    public CascadeFaceDetector(int resID, String resName) {
        resourceID = resID;
        resourceName = resName;
        loadClassifier();
    }

    @Override
    public MatOfRect detect(Mat mat) {
        MatOfRect faces = new MatOfRect();
        classifier.detectMultiScale(mat, faces);

        return faces;
    }

    private void loadClassifier() {
        File classifierFile = Utility.readResourceFile(resourceID, resourceName);

        if (classifierFile == null)
        {
            return;
        }

        classifier = new CascadeClassifier((classifierFile.getAbsolutePath()));
    }
}
