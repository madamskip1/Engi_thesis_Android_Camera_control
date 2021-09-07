package org.pw.engithesis.androidcameracontrol.facedetectors;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

public class HaarCascadeNativeBridge {

    private static final String TAG = "BridgeHaar";

    static {
        System.loadLibrary("facedetectors_haar");
    }

    private long nativeDetectorAddress;
    public CascadeClassifier classifier;

    public HaarCascadeNativeBridge(String cascadePath, int minFaceSize) {
        classifier = new CascadeClassifier(cascadePath);
        classifier.load(cascadePath);
        Log.i(TAG, "Classifier LOaded: " + classifier.empty());
    }

    public void detectFace(Mat grayFrame, MatOfRect resultFaces) {
        //nativeDetectFace(nativeDetectorAddress, grayFrame.getNativeObjAddr(), resultFaces.getNativeObjAddr());
        //test(grayFrame.getNativeObjAddr(), classifier.getNativeObjAddr(), resultFaces.getNativeObjAddr());
        classifier.detectMultiScale(grayFrame, resultFaces);
        Log.d(TAG, "DUPA ZNALEZIONE TWARZE: " + resultFaces.size());
    }

    public void release() {
        nativeReleaseDetector(nativeDetectorAddress);
    }

    private static native long nativeCreateDetector(String cascadePath, int minFaceSize);

    private static native void nativeDetectFace(long nativeAddress, long grayFrame, long resultFaces);

    private static native void nativeReleaseDetector(long nativeAddress);


    private static native void test(long grayFrameAddress, long classifierAddress, long resultFacesAddress);
}
