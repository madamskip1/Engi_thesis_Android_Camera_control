package com.example.kartykamer.facedetectors;

import android.content.Context;
import android.util.Log;

import com.example.kartykamer.App;
import com.example.kartykamer.R;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HaarCascadeFaceDetector extends FaceDetector {
    private File mCascadeFile;
    private CascadeClassifier classifier;

    public HaarCascadeFaceDetector() {
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
            Context context = App.getContext();
            InputStream is = context.getResources().openRawResource(R.raw.haarcascade_frontalface_default);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_default .xml");
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
