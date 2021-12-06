package org.pw.engithesis.androidcameracontrol;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class ImageSlider extends AppCompatActivity {

    int[] images = {R.drawable.portrait_test_1, R.drawable.author_name_text, R.drawable.portrait_test_2, R.drawable.lenna, R.drawable.portrait_test_4};
    ImageSliderPagerAdapter imageSliderPagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);

        viewPager = (ViewPager) findViewById(R.id.image_slider);

        imageSliderPagerAdapter = new ImageSliderPagerAdapter(ImageSlider.this, images);
        viewPager.setAdapter(imageSliderPagerAdapter);
        viewPager.setCurrentItem(images.length * ImageSliderPagerAdapter.LOOP_MULTIPLIER / 2);
    }
}