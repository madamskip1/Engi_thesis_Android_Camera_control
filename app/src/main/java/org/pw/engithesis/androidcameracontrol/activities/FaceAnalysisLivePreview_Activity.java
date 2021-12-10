package org.pw.engithesis.androidcameracontrol.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.pw.engithesis.androidcameracontrol.FPSCounter;
import org.pw.engithesis.androidcameracontrol.FaceAnalysisLivePreview;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.interfaces.Observer;

import java.util.Locale;

public class FaceAnalysisLivePreview_Activity extends AppCompatActivity implements Observer {

    ImageView imgView;
    FPSCounter fpsCounter;
    FaceAnalysisLivePreview faceAnalysisLivePreview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.main_activity_btn_text_live_analysis_preview);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_analysis_live_preview);
        imgView = findViewById(R.id.camera_preview);

        faceAnalysisLivePreview = new FaceAnalysisLivePreview(this);
        faceAnalysisLivePreview.setImageView(imgView);
        faceAnalysisLivePreview.start();

        fpsCounter = faceAnalysisLivePreview.getFpsCounter();
        fpsCounter.attach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void update() {
        setFPSText(fpsCounter.getCurFPS(), fpsCounter.getAvgFPS());
    }

    private void setFPSText(double curFPS, double avgFPS) {
        TextView curFPSTextView = findViewById(R.id.cur_fps_text);
        TextView avgFPSTextView = findViewById(R.id.avg_fps_text);

        curFPSTextView.setText(String.format(new Locale("pl", "PL"), "%.2f", curFPS));
        avgFPSTextView.setText(String.format(new Locale("pl", "PL"), "%.2f", avgFPS));
    }
}