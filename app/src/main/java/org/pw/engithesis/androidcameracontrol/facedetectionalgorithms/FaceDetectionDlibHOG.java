package org.pw.engithesis.androidcameracontrol.facedetectionalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.pw.engithesis.androidcameracontrol.Utility;

public class FaceDetectionDlibHOG implements FaceDetectionAlgorithm {

    static {
        System.loadLibrary("dlib_natives");
    }

    private final long nativeDetectorAddr;

    public FaceDetectionDlibHOG() {
        nativeDetectorAddr = initNativeDetector();
    }

    private static native long initNativeDetector();

    private static native void nativeDetect(long detectorAddr, long frameAddr, long resultFaces);

    @Override
    public Rect[] detect(Mat frame) {
        Mat dupa = new Mat();
        Imgproc.cvtColor(frame, dupa, Imgproc.COLOR_RGB2GRAY);

        MatOfRect faces = new MatOfRect();
        nativeDetect(nativeDetectorAddr, frame.getNativeObjAddr(), faces.getNativeObjAddr());


        Utility.drawRects(frame, faces.toArray(), new Scalar(75, 75, 75));
        return faces.toArray();
    }

}
