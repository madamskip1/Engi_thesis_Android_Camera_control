package org.pw.engithesis.androidcameracontrol.facedetectionalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.RawResourceManager;

public class FaceDetectionDlibMMOD implements FaceDetectionAlgorithm {
    private final long nativeDetectorAddr;

    static {
        System.loadLibrary("dlib_natives");
    }

    public FaceDetectionDlibMMOD() {
        RawResourceManager model = new RawResourceManager(R.raw.mmod_human_face_detector, "mmod_human_face_detector.dat");
        nativeDetectorAddr = init(model.getPath());
    }

    private static native long init(String modelPath);

    private static native void nativeDetect(long detectorAddr, long frameAddr, long resultFaces);

    @Override
    public Rect[] detect(Mat frame) {
        MatOfRect faces = new MatOfRect();
        nativeDetect(nativeDetectorAddr, frame.getNativeObjAddr(), faces.getNativeObjAddr());
        return faces.toArray();
    }
}