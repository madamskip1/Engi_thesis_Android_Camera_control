package org.pw.engithesis.androidcameracontrol.facemarksalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

public interface FacemarksAlgorithm {
    Point[] detect(Mat frame, Rect face);
}
