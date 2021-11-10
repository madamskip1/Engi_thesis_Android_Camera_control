package org.pw.engithesis.androidcameracontrol.landmarksalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.RawResourceManager;

public class DlibLandmarks implements LandmarksAlgorithm {
    public static final int LANDMARKS_NUM = 68;

    static {
        System.loadLibrary("dlib_natives");
    }

    private final long nativeDetectorAddr;

    public DlibLandmarks() {
        RawResourceManager model = new RawResourceManager(R.raw.shape_predictor_68_face_landmarks, "shape_predictor_68_face_landmarks.dat");
        nativeDetectorAddr = init(model.getPath());
    }

    private static native long init(String modelPath);

    private static native int[] getLandmarks(long detectorAddr, long frameAddr, int[] faceRect);

    @Override
    public Point[] detect(Mat frame, Rect face) {
        int[] faceRect = new int[]{face.x, face.y, face.width, face.height};
        int[] landmarksArray = getLandmarks(nativeDetectorAddr, frame.getNativeObjAddr(), faceRect);

        return intArrayLandmarkToPointArray(landmarksArray);
    }

    private Point[] intArrayLandmarkToPointArray(int[] landmarkArray) {
        Point[] landmarkPoint = new Point[LANDMARKS_NUM];
        for (int i = 0; i < LANDMARKS_NUM; i++) {
            int arrayIndex = i * 2;
            landmarkPoint[i] = new Point(landmarkArray[arrayIndex], landmarkArray[arrayIndex + 1]);
        }

        return landmarkPoint;
    }
}
