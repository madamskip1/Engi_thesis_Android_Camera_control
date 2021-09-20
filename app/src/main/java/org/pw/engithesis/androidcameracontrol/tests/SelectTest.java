package org.pw.engithesis.androidcameracontrol.tests;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import org.pw.engithesis.androidcameracontrol.R;

public class SelectTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_test);
    }

    public void goEyePupilImage(View view) {
        setContentView(R.layout.testing_layout);
        EyePupilDetectionImageTest test = new EyePupilDetectionImageTest(this, findViewById(R.id.testing_layout_root));
        test.createView();
    }
}