package org.pw.engithesis.androidcameracontrol.eyepupildetectionalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

public interface EyePupilDetectionAlgorithm {
    Point detect(Mat frame, Rect eyeRegion);
}
