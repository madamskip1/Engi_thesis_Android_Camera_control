package org.pw.engithesis.androidcameracontrol;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ImageViewsBuilder {
    ScrollView parentLayout;
    LinearLayout mainLinearLayout;
    Context ctx;

    public ImageViewsBuilder(Context context, ScrollView parent)
    {
        ctx = context;
        parentLayout = parent;
        parentLayout.requestLayout();

        mainLinearLayout = new LinearLayout(ctx);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(getScreenWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);

        mainLinearLayout.setLayoutParams(params);
        mainLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        mainLinearLayout.setOrientation(LinearLayout.VERTICAL);
    }

    public void addImage(Bitmap bitmap)
    {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int screenWidth = getScreenWidth();
        screenWidth *= 0.9;

        double ratio = (double)screenWidth / width;
        width = screenWidth;
        height *= ratio;

        ImageView imageView = new ImageView(ctx);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(height, width);
        params.bottomMargin = 20;
        imageView.setLayoutParams(params);

        imageView.setImageBitmap(bitmap);

        mainLinearLayout.addView(imageView);
    }

    public void build()
    {
        parentLayout.addView(mainLinearLayout);
    }


    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
}
