package org.pw.engithesis.androidcameracontrol.tests;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.pw.engithesis.androidcameracontrol.R;

public class SelectImageTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Wybór testu");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_imagetests);
    }

    public void goEyePupilDetector(View view) {
        setTitle("Detekcja źrenic");
        setContentView(R.layout.testing_layout);
        EyePupilDetectorImageTest test = new EyePupilDetectorImageTest(this, findViewById(R.id.testing_layout_root));
        test.createView();
    }

    public void goEARImage(View view) {
        setTitle("EAR - Eye aspect ratio");
        setContentView(R.layout.testing_layout);
        EARImageTest earImageTest = new EARImageTest(this, findViewById(R.id.testing_layout_root));
        earImageTest.createView();
    }

    public void goFaceDetector(View view) {
        setTitle("Detekcja twarzy");
        setContentView(R.layout.testing_layout);
        FaceDetectorImageTest faceDetectorImageTest = new FaceDetectorImageTest(this, findViewById(R.id.testing_layout_root));
        faceDetectorImageTest.createView();
    }

    public void goEyeDetector(View view) {
        setTitle("Detekcja oczu");
        setContentView(R.layout.testing_layout);
        EyeDetectorImageTest eyeDetectorImageTest = new EyeDetectorImageTest(this, findViewById(R.id.testing_layout_root));
        eyeDetectorImageTest.createView();
    }

    public void goFacemarks(View view) {
        setTitle("Detekcja facemarków");
        setContentView(R.layout.testing_layout);
        FacemarksImageTest facemarksImageTest = new FacemarksImageTest(this, findViewById(R.id.testing_layout_root));
        facemarksImageTest.createView();
    }
}