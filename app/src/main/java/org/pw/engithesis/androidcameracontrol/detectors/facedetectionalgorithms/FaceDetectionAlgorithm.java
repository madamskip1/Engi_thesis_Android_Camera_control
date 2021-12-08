package org.pw.engithesis.androidcameracontrol.detectors.facedetectionalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public interface FaceDetectionAlgorithm {
    Rect[] detect(Mat frame);
}
