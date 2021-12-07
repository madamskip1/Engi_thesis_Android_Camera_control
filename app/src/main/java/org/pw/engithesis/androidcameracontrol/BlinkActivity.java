package org.pw.engithesis.androidcameracontrol;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.pw.engithesis.androidcameracontrol.interfaces.Observer;

public class BlinkActivity extends AppCompatActivity implements Observer {

    CameraControlMainClass cameraControlMainClass;
    EyeBlinkDetector blinkDetector;
    private BlinkNotificationAdapter blinkNotificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blink);

        blinkNotificationAdapter = new BlinkNotificationAdapter(this);
        cameraControlMainClass = new CameraControlMainClass(this);
        blinkDetector = cameraControlMainClass.attachObserverToBlinkDetector(this);
        cameraControlMainClass.start();
    }

    private void addBox() {
        blinkNotificationAdapter.addBox();
    }


    @Override
    public void update() {
        addBox();
    }
}