package org.pw.engithesis.androidcameracontrol.eyepupildetectionalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public abstract class EyePupilDetectionAlgorithm {
    public abstract Point detect(Mat frame, Rect eyeRegion);

    protected Mat getGrayEyeMat(Mat frame, Rect eyeRegion) {
        Mat eyeMat = frame.submat(eyeRegion);
        Mat grayEyeMat = new Mat();
        Imgproc.cvtColor(eyeMat, grayEyeMat, Imgproc.COLOR_RGB2GRAY);

        return grayEyeMat;
    }

    protected Point calcPointInFrame(Point eye, Rect eyeRegion) {
        eye.x += eyeRegion.x;
        eye.y += eyeRegion.y;

        return eye;
    }
}
