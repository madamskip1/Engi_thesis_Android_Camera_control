package org.pw.engithesis.androidcameracontrol.detectors.eyedetectionalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public interface EyeDetectionAlgorithm {
    Rect[] detect(Mat eyesRegionMat);
}
