package org.pw.engithesis.androidcameracontrol.detectors.facedetectionalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;

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
        MatOfRect faces = new MatOfRect();
        nativeDetect(nativeDetectorAddr, frame.getNativeObjAddr(), faces.getNativeObjAddr());
        return faces.toArray();
    }
}