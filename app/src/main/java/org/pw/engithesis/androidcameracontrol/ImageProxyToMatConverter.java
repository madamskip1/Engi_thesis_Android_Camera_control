package org.pw.engithesis.androidcameracontrol;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.Image;

import androidx.camera.core.ImageProxy;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;

public class ImageProxyToMatConverter {
    Image frame;
    Mat grayMat = null;
    Mat rgbaMat = null;

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
        // TODO: zapisać raz szerokosc i wysokoc. Może w strukturze. Później pewnie będą przy analizie potrzebne.
        int w = frame.getWidth();
        int h = frame.getHeight();

        ByteBuffer y_plane = planes[0].getBuffer();
        int y_plane_step = planes[0].getRowStride();
        grayMat = new Mat(h, w, CvType.CV_8UC1, y_plane, y_plane_step);

        rotateAndFlip(grayMat);

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

    public Mat rgb()
    {
        byte[] nv21;
        ByteBuffer yBuffer = frame.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = frame.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = frame.getPlanes()[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        nv21 = new byte[ySize + uSize + vSize];

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        Mat mYuv = new Mat(frame.getHeight() + frame.getHeight() / 2, frame.getWidth(), CvType.CV_8UC1);
        mYuv.put(0, 0, nv21);
        Mat mRGB = new Mat();
        Imgproc.cvtColor(mYuv, mRGB, Imgproc.COLOR_YUV2RGB_NV21, 3);

        rotateAndFlip(mRGB);
        return mRGB;
    }

    public Bitmap rgbaBitmap() {
        return createBitmap(rgba());
    }

    public Bitmap grayBitmap() {
        return createBitmap(gray());
    }


    public Bitmap createBitmap(Mat mat) {
        Bitmap bm = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bm);

        return bm;
    }

    private void rotateAndFlip(Mat matToRotate) {
        Core.rotate(matToRotate, matToRotate, Core.ROTATE_90_COUNTERCLOCKWISE);
        Core.flip(matToRotate, matToRotate, 1);
    }
}
