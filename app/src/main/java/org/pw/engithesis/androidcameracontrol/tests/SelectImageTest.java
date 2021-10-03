package org.pw.engithesis.androidcameracontrol.tests;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.pw.engithesis.androidcameracontrol.R;

public class SelectImageTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Image Test Selection");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_imagetests);
    }

    public void goEyePupilDetector(View view) {
        setTitle("Eye Pupil Detector Image Test");
        setContentView(R.layout.testing_layout);
        EyePupilDetectorImageTest test = new EyePupilDetectorImageTest(this, findViewById(R.id.testing_layout_root));
        test.createView();
    }

    public void goEARImage(View view) {
        setTitle("EAR Image Test");
        setContentView(R.layout.testing_layout);
        EARImageTest earImageTest = new EARImageTest(this, findViewById(R.id.testing_layout_root));
        earImageTest.createView();
    }

    public void goFaceDetector(View view) {
        setTitle("Face Detector Image Test");
        setContentView(R.layout.testing_layout);
        FaceDetectorImageTest faceDetectorImageTest = new FaceDetectorImageTest(this, findViewById(R.id.testing_layout_root));
        faceDetectorImageTest.createView();
    }


    public void goEyeDetector(View view) {
        setTitle("Eye Detector Image Test");
        setContentView(R.layout.testing_layout);
        EyeDetectorImageTest eyeDetectorImageTest = new EyeDetectorImageTest(this, findViewById(R.id.testing_layout_root));
        eyeDetectorImageTest.createView();
    }
}