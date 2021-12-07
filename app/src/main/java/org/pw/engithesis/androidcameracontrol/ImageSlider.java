package org.pw.engithesis.androidcameracontrol;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import org.pw.engithesis.androidcameracontrol.interfaces.Observer;

public class ImageSlider extends AppCompatActivity implements Observer {

    int[] images = {R.drawable.portrait_test_1, R.drawable.author_name_text, R.drawable.portrait_test_2, R.drawable.lenna, R.drawable.portrait_test_4};
    ImageSliderPagerAdapter imageSliderPagerAdapter;
    ViewPager viewPager;
    CameraControlMainClass cameraControlMainClass;
    EyeMoveDetector eyeMoveDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);

        cameraControlMainClass = new CameraControlMainClass(this);
        viewPager = (ViewPager) findViewById(R.id.image_slider);

        imageSliderPagerAdapter = new ImageSliderPagerAdapter(ImageSlider.this, images);
        viewPager.setAdapter(imageSliderPagerAdapter);
        viewPager.setCurrentItem(images.length * ImageSliderPagerAdapter.LOOP_MULTIPLIER / 2);

        eyeMoveDetector = cameraControlMainClass.attachObserverToEyeMoveDetector(this);
        cameraControlMainClass.start();
    }

    public void nextImg() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    public void prevImg() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
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