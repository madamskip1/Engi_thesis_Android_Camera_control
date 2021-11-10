package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.pw.engithesis.androidcameracontrol.landmarksalgorithms.DlibLandmarks;
import org.pw.engithesis.androidcameracontrol.landmarksalgorithms.LandmarksAlgorithm;

public class LandmarksDetector {
    private static final int RIGHT_EYE_START = 36;
    private static final int LEFT_EYE_START = 42;
    private static final int EYE_LENGTH = 6;
    private final LandmarksAlgorithm landmarksAlgorithm;
    private Point[] landmarks;

    public LandmarksDetector() {
        this(new DlibLandmarks());
    }

    public LandmarksDetector(LandmarksAlgorithm landmarksAlgorithm) {
        this.landmarksAlgorithm = landmarksAlgorithm;
    }

    public Point[] detect(Mat frame, Rect face) {
        landmarks = landmarksAlgorithm.detect(frame, face);
        return landmarks;
    }

    public void drawLandmarks(Mat mat) {
        for (Point landmark : landmarks) {
            Imgproc.circle(mat, landmark, 2, new Scalar(0, 200, 0), 1);
        }
    }

    public Point[] getLeftEyeLandmarks() {
        return getEyeLandmarks(LEFT_EYE_START);
    }

    public Point[] getRightEyeLandmarks() {
        return getEyeLandmarks(RIGHT_EYE_START);
    }


    private Point[] getEyeLandmarks(int landmarkStart) {
        Point[] eye = new Point[EYE_LENGTH];
        System.arraycopy(landmarks, landmarkStart, eye, 0, EYE_LENGTH);
        return eye;
    }
}
