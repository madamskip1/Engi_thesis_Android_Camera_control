package org.pw.engithesis.androidcameracontrol.detectors;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.pw.engithesis.androidcameracontrol.detectors.facemarksdetectionalgorithms.FacemarksAlgorithm;
import org.pw.engithesis.androidcameracontrol.detectors.facemarksdetectionalgorithms.FacemarksKazemi;

public class FacemarksDetector {
    private static final int RIGHT_EYE_START_INDEX = 36;
    private static final int LEFT_EYE_START_INDEX = 42;
    private static final int EYE_LENGTH = 6;

    private final FacemarksAlgorithm facemarksAlgorithm;
    private Point[] facemarks;

    public FacemarksDetector() {
        this(new FacemarksKazemi());
    }

    public FacemarksDetector(FacemarksAlgorithm facemarksAlgorithm) {
        this.facemarksAlgorithm = facemarksAlgorithm;
    }

    public Point[] detect(Mat frame, Rect face) {
        facemarks = facemarksAlgorithm.detect(frame, face);
        return facemarks;
    }

    public void drawFacemarks(Mat mat) {
        for (Point facemark : facemarks) {
            Imgproc.circle(mat, facemark, 1, new Scalar(0, 255, 0), 1);
        }
    }

    public Point[] getLeftEyeFacemarks() {
        return getEyeFacemarks(LEFT_EYE_START_INDEX);
    }

    public Point[] getRightEyeFacemarks() {
        return getEyeFacemarks(RIGHT_EYE_START_INDEX);
    }


    private Point[] getEyeFacemarks(int facemarksStartIndex) {
        Point[] eye = new Point[EYE_LENGTH];
        System.arraycopy(facemarks, facemarksStartIndex, eye, 0, EYE_LENGTH);
        return eye;
    }
}
