package org.pw.engithesis.androidcameracontrol;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ViewsBuilder {
    ScrollView parentLayout;
    LinearLayout mainLinearLayout;
    LinearLayout curSection;
    Context ctx;

    public ViewsBuilder(Context context, ScrollView parent)
    {
        ctx = context;
        parentLayout = parent;
        parentLayout.requestLayout();

        mainLinearLayout = newLinearLayout();
        mainLinearLayout.getLayoutParams().width = getScreenWidth();

        curSection = mainLinearLayout;
    }

    public void newSection()
    {
        LinearLayout section = newLinearLayout();
        curSection.addView(section);

        curSection = section;
    }

    public void closeSection()
    {
        curSection = (LinearLayout) curSection.getParent();
    }

    public void addImage(Bitmap bitmap)
    {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int screenWidth = getScreenWidth();
        //screenWidth *= 0.9;

        double ratio = (double)screenWidth / width;
        width = screenWidth;
        height *= ratio;

        ImageView imageView = new ImageView(ctx);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.bottomMargin = 20;
        imageView.setLayoutParams(params);

        imageView.setImageBitmap(bitmap);

        curSection.addView(imageView);
    }

    public void addText(String text)
    {
        TextView textView = new TextView(ctx);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);

        textView.setText(text);
        curSection.addView(textView);
    }

    public void build()
    {

        parentLayout.addView(mainLinearLayout);
        parentLayout.invalidate();
        parentLayout.requestLayout();
    }


    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private LinearLayout newLinearLayout()
    {
        LinearLayout layout = new LinearLayout(ctx);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layout.setLayoutParams(params);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setOrientation(LinearLayout.VERTICAL);

        return layout;
    }
}
