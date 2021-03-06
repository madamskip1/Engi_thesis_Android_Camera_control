package org.pw.engithesis.androidcameracontrol.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import org.pw.engithesis.androidcameracontrol.FaceAnalyser;
import org.pw.engithesis.androidcameracontrol.ImageSliderPagerAdapter;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.detectors.EyeMoveDetector;
import org.pw.engithesis.androidcameracontrol.interfaces.Observer;

public class ImageSlider extends AppCompatActivity implements Observer {

    int[] images = {
            R.drawable.slider_img_1,
            R.drawable.slider_img_2,
            R.drawable.slider_img_3,
            R.drawable.slider_img_4,
            R.drawable.slider_img_5,
            R.drawable.slider_img_6,
            R.drawable.slider_img_7,
            R.drawable.slider_img_8,
            R.drawable.slider_img_9,
            R.drawable.slider_img_10
    };
    ViewPager viewPager;
    TextView moveMsgInfo;
    FaceAnalyser faceAnalyser;
    EyeMoveDetector eyeMoveDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.main_activity_btn_text_eye_move);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);

        moveMsgInfo = findViewById(R.id.image_slider_move_info_text);
        viewPager = findViewById(R.id.image_slider);
        viewPager.setAdapter(new ImageSliderPagerAdapter(ImageSlider.this, images));
        viewPager.setCurrentItem(images.length * ImageSliderPagerAdapter.LOOP_MULTIPLIER / 2);

        faceAnalyser = new FaceAnalyser(this);
        eyeMoveDetector = faceAnalyser.attachObserverToEyeMoveDetector(this);
        faceAnalyser.start();
    }

    @SuppressLint("SetTextI18n")
    public void nextImg() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        moveMsgInfo.setText("Przesunięto w prawo");
    }

    @SuppressLint("SetTextI18n")
    public void prevImg() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        moveMsgInfo.setText("Przesunięto w lewo");
    }

    @Override
    public void update() {
        EyeMoveDetector.EYE_MOVE eyesMoved = eyeMoveDetector.getEyeMovedTo();
        if (eyesMoved == EyeMoveDetector.EYE_MOVE.RIGHT) {
            nextImg();
        } else if (eyesMoved == EyeMoveDetector.EYE_MOVE.LEFT) {
            prevImg();
        }
    }
}