package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.eyepupildetectionalgorithms.EyePupilDetectionAlgorithm;
import org.pw.engithesis.androidcameracontrol.eyepupildetectionalgorithms.EyePupilDetectionCDF;

public class EyePupilDetector {
    private final EyePupilDetectionAlgorithm detectionAlgorithm;

    public EyePupilDetector() {
        // CDF method is default eye detection algorithm
        this(new EyePupilDetectionCDF());
    }

    public EyePupilDetector(EyePupilDetectionAlgorithm detectionAlgorithm) {
        this.detectionAlgorithm = detectionAlgorithm;
    }

    public Point detect(Mat frame, Rect eyeRegion) {
        return detectionAlgorithm.detect(frame, eyeRegion);
    }
}
