package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.eyedetectionalgorithms.EyeDetectionAlgorithm;
import org.pw.engithesis.androidcameracontrol.eyedetectionalgorithms.EyeDetectionHaar;

public class EyeDetector {
    public static final int RIGHT_EYE_INDEX = 0;
    public static final int LEFT_EYE_INDEX = 1;
    private static final double cropTop = 0.1;
    private static final double cropBottom = 0.45;
    private static final double cropLeft = 0.1;
    private static final double cropRight = 0.1;
    private final EyeFilter eyeFilter;
    private final EyeDetectionAlgorithm detectionAlgorithm;

    public EyeDetector() {
        // Haar Cascading Classifier is default eye detection algorithm
        this(new EyeDetectionHaar());
    }

    public EyeDetector(EyeDetectionAlgorithm eyeDetectionAlgorithm) {
        this.detectionAlgorithm = eyeDetectionAlgorithm;
        eyeFilter = new EyeFilter();
    }

    public Rect[] detect(Mat mat, Rect face) {
        Rect eyesRegion = getEyesRegionRect(face);
        Mat eyesRegionMat = mat.submat(eyesRegion);

        Rect[] eyes = detectionAlgorithm.detect(eyesRegionMat);
        eyes = eyeFilter.filter(eyes, eyesRegion);

        for (Rect eye : eyes) {
            if (eye != null) {
                eye.y += eyesRegion.y;
                eye.x += eyesRegion.x;
            }
        }

        return eyes;
    }

    private Rect getEyesRegionRect(Rect face) {
        int eyesRegionX = face.x + (int) (face.width * cropLeft);
        int eyesRegionY = face.y + (int) (face.height * cropTop);
        int eyesRegionWidth = (int) (face.width * (1.0 - cropRight - cropLeft));
        int eyesRegionHeight = (int) (face.height * (1.0 - cropBottom - cropTop));

        return new Rect(eyesRegionX, eyesRegionY, eyesRegionWidth, eyesRegionHeight);
    }
}
