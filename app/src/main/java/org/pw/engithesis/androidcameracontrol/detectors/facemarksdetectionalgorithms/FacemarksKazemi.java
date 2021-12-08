package org.pw.engithesis.androidcameracontrol.detectors.facemarksdetectionalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.RawResourceManager;

public class FacemarksKazemi implements FacemarksAlgorithm {
    public static final int FACEMARKS_NUM = 68;

    static {
        System.loadLibrary("dlib_natives");
    }

    private final long nativeDetectorAddr;

    public FacemarksKazemi() {
        RawResourceManager model = new RawResourceManager(R.raw.shape_predictor_68_face_landmarks, "shape_predictor_68_face_landmarks.dat");
        nativeDetectorAddr = init(model.getPath());
    }

    private static native long init(String modelPath);

    private static native int[] getFacemarks(long detectorAddr, long frameAddr, int[] faceRect);

    @Override
    public Point[] detect(Mat frame, Rect face) {
        int[] faceRect = new int[]{face.x, face.y, face.width, face.height};
        int[] facemarskArray = getFacemarks(nativeDetectorAddr, frame.getNativeObjAddr(), faceRect);

        return intArrayFacemarksToPointArray(facemarskArray);
    }

    private Point[] intArrayFacemarksToPointArray(int[] facemarksArray) {
        Point[] facemarksPoints = new Point[FACEMARKS_NUM];
        for (int i = 0; i < FACEMARKS_NUM; i++) {
            int arrayIndex = i * 2;
            facemarksPoints[i] = new Point(facemarksArray[arrayIndex], facemarksArray[arrayIndex + 1]);
        }

        return facemarksPoints;
    }
}
