package com.example.kartykamer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.util.Log;

import androidx.camera.core.ImageProxy;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class CameraXToOpenCV {
    private static final String TAG = "CameraXToOpenCV";
    Image frame;
    Mat grayMat = null;
    Mat rgbaMat = null;

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
        grayMat = null;
        rgbaMat = null;
    }

    public Mat gray() {
        if (grayMat != null) {
            return grayMat;
        }

        Image.Plane[] planes = frame.getPlanes();
        int w = frame.getWidth();
        int h = frame.getHeight();

        ByteBuffer y_plane = planes[0].getBuffer();
        int y_plane_step = planes[0].getRowStride();
        grayMat = new Mat(h, w, CvType.CV_8UC1, y_plane, y_plane_step);

        rotateAndFlip(grayMat);

        Log.d(TAG, "gray done");
        return grayMat;
    }

    public Mat rgba() {
        if (rgbaMat != null) {
            return rgbaMat;
        }
        rgbaMat = new Mat();

        Image.Plane[] planes = frame.getPlanes();
        int w = frame.getWidth();
        int h = frame.getHeight();

        ByteBuffer y_plane = planes[0].getBuffer();
        int y_plane_step = planes[0].getRowStride();
        ByteBuffer uv_plane1 = planes[1].getBuffer();
        int uv_plane1_step = planes[1].getRowStride();
        ByteBuffer uv_plane2 = planes[2].getBuffer();
        int uv_plane2_step = planes[2].getRowStride();
        Mat y_mat = new Mat(h, w, CvType.CV_8UC1, y_plane, y_plane_step);
        Mat uv_mat1 = new Mat(h / 2, w / 2, CvType.CV_8UC2, uv_plane1, uv_plane1_step);
        Mat uv_mat2 = new Mat(h / 2, w / 2, CvType.CV_8UC2, uv_plane2, uv_plane2_step);
        long addr_diff = uv_mat2.dataAddr() - uv_mat1.dataAddr();
        if (addr_diff > 0) {
            Imgproc.cvtColorTwoPlane(y_mat, uv_mat1, rgbaMat, Imgproc.COLOR_YUV2RGBA_NV12);
        } else {
            Imgproc.cvtColorTwoPlane(y_mat, uv_mat2, rgbaMat, Imgproc.COLOR_YUV2RGBA_NV21);
        }

        rotateAndFlip(rgbaMat);

        return rgbaMat;
    }

    public Bitmap rgbaBitmap() {
        rgba();
        return createBitmap(rgbaMat);
    }

    public Bitmap grayBitmap() {
        gray();
        MatOfRect faces = new MatOfRect();
        classifier.detectMultiScale(grayMat, faces);
        Log.e(TAG, "Znalezione twarze: " + faces.size());

        org.opencv.core.Rect[] facesArray = faces.toArray();
        Log.d(TAG, "ZNaleziono twarzy: " + facesArray.length);
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(grayMat, facesArray[i].tl(), facesArray[i].br(), new Scalar(255, 0, 0), 3);

        return createBitmap(grayMat);
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

    private void rotateAndFlip(Mat matToRotate) {
        Core.rotate(matToRotate, matToRotate, Core.ROTATE_90_COUNTERCLOCKWISE);
        Core.flip(matToRotate, matToRotate, 1);
    }
}
