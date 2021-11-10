package org.pw.engithesis.androidcameracontrol.landmarksalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

public interface LandmarksAlgorithm {
    Point[] detect(Mat frame, Rect face);
}
