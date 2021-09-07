package org.pw.engithesis.androidcameracontrol.facedetectors;

import android.content.Context;
import android.util.Log;

import org.pw.engithesis.androidcameracontrol.App;
import org.pw.engithesis.androidcameracontrol.R;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CascadeFaceDetector extends FaceDetector {
    private File mCascadeFile;
    private CascadeClassifier classifier;
    int resourceID = -1;
    String resourceName = null;

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
        try {
            if (resourceID == -1 || resourceName == null) {
                return;
            }

            Context context = App.getContext();
            InputStream is = context.getResources().openRawResource(resourceID);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, resourceName);
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            classifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
