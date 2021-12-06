package org.pw.engithesis.androidcameracontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.Objects;

public class ImageSliderPagerAdapter extends PagerAdapter {
    public static final int LOOP_MULTIPLIER = 30;
    private final int[] images;
    private final LayoutInflater layoutInflater;

    public ImageSliderPagerAdapter(Context ctx, int[] images) {
        this.images = images;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.length * LOOP_MULTIPLIER;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.image_slider_item, container, false);
        ImageView itemImageview = (ImageView) itemView.findViewById(R.id.image_slider_item);
        itemImageview.setImageResource(images[calcPosition(position)]);
        Objects.requireNonNull(container).addView(itemView);


        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    private int calcPosition(int pos) {
        return pos % images.length;
    }
}
