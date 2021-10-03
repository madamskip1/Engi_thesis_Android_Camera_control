package org.pw.engithesis.androidcameracontrol.tests;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ScrollView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.pw.engithesis.androidcameracontrol.DrawableResourceManager;
import org.pw.engithesis.androidcameracontrol.ViewsBuilder;


public abstract class ImageTest {
    protected Context ctx;
    protected ScrollView parentView;

    protected ImageTest(Context context, ScrollView parent) {
        OpenCVLoader.initDebug();
        ctx = context;
        parentView = parent;
    }

    public abstract void createView();

    protected void addImageToView(Mat mat, ViewsBuilder viewsBuilder) {
        Bitmap temp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, temp);

        viewsBuilder.addImage(temp);
    }

    protected Mat getImageMat(int imageID) {
        DrawableResourceManager drawableResourceManager = new DrawableResourceManager(imageID);
        return drawableResourceManager.getRGBMat();
    }
}
