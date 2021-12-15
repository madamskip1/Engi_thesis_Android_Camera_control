package org.pw.engithesis.androidcameracontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageProxy;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.detectors.EyeBlinkDetector;
import org.pw.engithesis.androidcameracontrol.detectors.EyeMoveDetector;
import org.pw.engithesis.androidcameracontrol.detectors.EyePupilDetector;
import org.pw.engithesis.androidcameracontrol.detectors.FaceDetector;
import org.pw.engithesis.androidcameracontrol.detectors.FacemarksDetector;
import org.pw.engithesis.androidcameracontrol.detectors.eyedetectionalgorithms.EyeDetectionFacemarks;
import org.pw.engithesis.androidcameracontrol.interfaces.Observer;

public class FaceAnalyser extends ImageAnalyser {

    private final FaceDetector faceDetector = new FaceDetector();
    private final FacemarksDetector facemarksDetector = new FacemarksDetector();
    private final EyeDetectionFacemarks eyeDetector = new EyeDetectionFacemarks();
    private final EyePupilDetector eyePupilDetector = new EyePupilDetector();
    private final EARClosedEyeDetector closedEyeDetector = new EARClosedEyeDetector();
    private final EyeMoveDetector eyeMoveDetector = new EyeMoveDetector();
    private final EyeBlinkDetector blinkDetector = new EyeBlinkDetector();
    ImageProxyToMatConverter proxyConverter = new ImageProxyToMatConverter();

    public FaceAnalyser(AppCompatActivity activity) {
        super(activity);
    }

    public EyeMoveDetector attachObserverToEyeMoveDetector(Observer observer) {
        eyeMoveDetector.attach(observer);
        return eyeMoveDetector;
    }

    public EyeBlinkDetector attachObserverToBlinkDetector(Observer observer) {
        blinkDetector.attach(observer);
        return blinkDetector;
    }

    @Override
    public void analyse(ImageProxy image) {
        proxyConverter.setFrame(image);
        Mat frameRGB = proxyConverter.rgb();

        Rect face = faceDetector.detect(frameRGB);
        if (face != null) {
            facemarksDetector.detect(frameRGB, face);
            Point[] rightEyeLandmarks = facemarksDetector.getRightEyeFacemarks();
            Point[] leftEyeLandmarks = facemarksDetector.getLeftEyeFacemarks();
            boolean eyesClosed = closedEyeDetector.areClosed(leftEyeLandmarks, rightEyeLandmarks);

            if (!eyesClosed) {
                Rect[] eyes = eyeDetector.detect(rightEyeLandmarks, leftEyeLandmarks);
                Point[] eyesPupil = new Point[2];

                for (int i = 0; i < 2; i++) {
                    eyesPupil[i] = eyePupilDetector.detect(frameRGB, eyes[i]);
                }

                eyeMoveDetector.tickEyesOpen(eyesPupil, eyes);
            } else
                eyeMoveDetector.tickEyeClosed();

            blinkDetector.checkEyeBlink(eyesClosed);
        } else {
            eyeMoveDetector.tickEyeClosed();
            blinkDetector.checkEyeBlink(true);
        }
    }
}
