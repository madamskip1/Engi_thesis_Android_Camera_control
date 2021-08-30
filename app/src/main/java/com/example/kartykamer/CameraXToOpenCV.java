package com.example.kartykamer;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageProxy;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class CameraXToOpenCV {
    private static final String TAG = "CameraXToOpenCV";
    Image frame;
    Mat matGray = null;
    int degrees;
    private File mCascadeFile;
    private CascadeClassifier classifier;

    @SuppressLint("UnsafeOptInUsageError")
    @androidx.camera.core.ExperimentalGetImage
    public CameraXToOpenCV(ImageProxy imageProxy) {
        OpenCVLoader.initDebug();
        Log.d(TAG, "Constuctor");
        frame = imageProxy.getImage();
        Log.d(TAG, "get image done");
    }

    public CameraXToOpenCV() {
        OpenCVLoader.initDebug();
        Log.d(TAG, "Utworzono CameraXToOpencV");
        try {
            loadClassifier();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ;

    private void loadClassifier() throws IOException {
        Context context = App.getContext();
        InputStream is = context.getResources().openRawResource(R.raw.haarcascade_frontalface_default);
        File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
        mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_default .xml");
        FileOutputStream os = new FileOutputStream(mCascadeFile);

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        is.close();
        os.close();

        classifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        if (classifier.empty()) {
            Log.e(TAG, "Failed to load cascade classifier");
            classifier = null;
        } else
            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());


        cascadeDir.delete();
    }


    @SuppressLint("UnsafeOptInUsageError")
    public void setFrame(ImageProxy imageProxy) {
        frame = imageProxy.getImage();
        degrees = imageProxy.getImageInfo().getRotationDegrees();
        Log.e(TAG, "Zdjecie obrocone: " + degrees);
    }

    public Mat gray() {
        Image.Plane[] planes = frame.getPlanes();
        int w = frame.getWidth();
        int h = frame.getHeight();
        assert (planes[0].getPixelStride() == 1);

        ByteBuffer y_plane = planes[0].getBuffer();
        int y_plane_step = planes[0].getRowStride();
        matGray = new Mat(h, w, CvType.CV_8UC1, y_plane, y_plane_step);

        Core.rotate(matGray, matGray, Core.ROTATE_90_COUNTERCLOCKWISE);
        Core.flip(matGray, matGray, 1);

        Log.d(TAG, "gray done");
        return matGray;
    }

    public Bitmap grayBitmap() {
        gray();
        MatOfRect faces = new MatOfRect();
        classifier.detectMultiScale(matGray, faces);
        Log.e(TAG, "Znalezione twarze: " + faces.size());

        org.opencv.core.Rect[] facesArray = faces.toArray();
        Log.d(TAG, "ZNaleziono twarzy: " + facesArray.length);
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(matGray, facesArray[i].tl(), facesArray[i].br(), new Scalar(255, 0, 0), 3);

        return createBitmap(matGray);
    }

    private Bitmap createBitmap(Mat mat) {
        Bitmap bm = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bm);

        return bm;
    }

    public Bitmap toBitmap() {
        Image.Plane[] planes = frame.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, frame.getWidth(), frame.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }


    public int getRotation() {
        if (degrees == 270)
            return Core.ROTATE_90_COUNTERCLOCKWISE;
        else if (degrees == 0)
            return Core.ROTATE_90_CLOCKWISE;
        else if (degrees == 90)
            return Core.ROTATE_90_CLOCKWISE;
        else
            return Core.ROTATE_180;
    }

}
