package org.pw.engithesis.androidcameracontrol;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.pw.engithesis.androidcameracontrol.interfaces.Observer;

public class CameraX_test extends AppCompatActivity implements Observer {
    ImageView imgView;
    FPSCounter fpsCounter;
    CameraCore cameraCore;

    FileLogger fileLogger;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("CAMERAX_TEST", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_image_view);
        imgView = (ImageView) findViewById(R.id.camera_preview);
        fileLogger = new FileLogger("test.txt", true);
        cameraCore = new CameraCore(this);
        cameraCore.setImageView(imgView);
        cameraCore.start();

        fpsCounter = cameraCore.getFpsCounter();
        fpsCounter.attach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fileLogger.close();
    }

    @Override
    public void update() {
        setFPSText(fpsCounter.getCurFPS(), fpsCounter.getAvgFPS());
    }

    private void setFPSText(double curFPS, double avgFPS) {
        TextView curFPSTextView = (TextView) findViewById(R.id.cur_fps_text);
        TextView avgFPSTextView = (TextView) findViewById(R.id.avg_fps_text);

        curFPSTextView.setText(String.format("%.2f", curFPS));
        avgFPSTextView.setText(String.format("%.2f", avgFPS));

        fileLogger.write("Cur: " + curFPS + ", avg: " + avgFPS);
    }
}