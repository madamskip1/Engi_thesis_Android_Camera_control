package org.pw.engithesis.androidcameracontrol;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageProxy;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.pw.engithesis.androidcameracontrol.detectors.EyeBlinkDetector;
import org.pw.engithesis.androidcameracontrol.detectors.EyeMoveDetector;
import org.pw.engithesis.androidcameracontrol.detectors.EyePupilDetector;
import org.pw.engithesis.androidcameracontrol.detectors.FaceDetector;
import org.pw.engithesis.androidcameracontrol.detectors.FacemarksDetector;
import org.pw.engithesis.androidcameracontrol.detectors.eyedetectionalgorithms.EyeDetectionFacemarks;

public class FaceAnalysisLivePreview extends ImageAnalyser {
    private final FaceDetector faceDetector = new FaceDetector();
    private final FPSCounter fpsCounter = new FPSCounter();
    private final EyePupilDetector pupilDetector = new EyePupilDetector();
    private final FacemarksDetector facemarksDetector = new FacemarksDetector();
    private final EyeBlinkDetector blinkDetector = new EyeBlinkDetector();
    private final EyeDetectionFacemarks eyeDetectionFacemarks = new EyeDetectionFacemarks();
    private final EARClosedEyeDetector closedEyeDetector = new EARClosedEyeDetector();
    private final EyeMoveDetector eyeMoveDetector = new EyeMoveDetector();
    private ImageView imgView = null;

    public FaceAnalysisLivePreview(AppCompatActivity context) {
        super(context);
    }

    @Override
    void analyse(ImageProxy image) {
        fpsCounter.tick();

        proxyConverter.setFrame(image);
        Mat frameRGB = proxyConverter.rgb();
        Mat outputFrame = frameRGB.clone();

        Rect face = faceDetector.detect(frameRGB);
        if (face != null) {
            Utility.drawRect(outputFrame, face, new Scalar(140, 140, 140), 1);
            facemarksDetector.detect(frameRGB, face);
            facemarksDetector.drawFacemarks(outputFrame);
            Point[] rightEyeLandmarks = facemarksDetector.getRightEyeFacemarks();
            Point[] leftEyeLandmarks = facemarksDetector.getLeftEyeFacemarks();

            boolean eyesClosed = closedEyeDetector.areClosed(leftEyeLandmarks, rightEyeLandmarks);
            if (!eyesClosed) {
                Rect[] eyes = eyeDetectionFacemarks.detect(rightEyeLandmarks, leftEyeLandmarks);
                Utility.drawRects(outputFrame, eyes);
                Point[] eyesPupil = new Point[2];

                for (int i = 0; i < 2; i++) {
                    Rect eye = eyes[i];
                    eyesPupil[i] = pupilDetector.detect(frameRGB, eye);
                    Imgproc.circle(outputFrame, eyesPupil[i], 2, new Scalar(200, 200, 200), 2);

                    // Draw EyeMove borders inside eye rect
                    int x1 = (int) (eye.x + (eye.width * 0.4));
                    int x2 = (int) (eye.x + (eye.width * 0.6));
                    int yTop = eye.y - 10;
                    int yBottom = eye.y + eye.height + 10;
                    Utility.clamp(eye.y, 0, eye.y);
                    Utility.clamp(eye.y, eye.y, outputFrame.height());

                    Imgproc.line(outputFrame, new Point(x1, yTop), new Point(x1, yBottom), new Scalar(255, 0, 0), 1);
                    Imgproc.line(outputFrame, new Point(x2, yTop), new Point(x2, yBottom), new Scalar(255, 0, 0), 1);
                }

                EyeMoveDetector.EYE_MOVE eyesMoved = eyeMoveDetector.tickEyesOpen(eyesPupil, eyes);
                if (eyesMoved == EyeMoveDetector.EYE_MOVE.CENTER) {
                    Imgproc.circle(outputFrame, new Point(outputFrame.width() / 2.0, 60), 30, new Scalar(50, 50, 255), 30);
                } else if (eyesMoved == EyeMoveDetector.EYE_MOVE.LEFT) {
                    Imgproc.circle(outputFrame, new Point(60, 60), 30, new Scalar(50, 50, 255), 30);
                } else if (eyesMoved == EyeMoveDetector.EYE_MOVE.RIGHT) {
                    Imgproc.circle(outputFrame, new Point(outputFrame.width() - 60, 60), 30, new Scalar(50, 50, 255), 30);
                }
            } else {
                eyeMoveDetector.tickEyeClosed();
            }

            boolean blink = blinkDetector.checkEyeBlink(eyesClosed);

            if (blink) {
                Imgproc.circle(outputFrame, new Point(outputFrame.width() / 2.0, outputFrame.height() / 2.0), 30, new Scalar(255, 50, 50), 30);
            }

        } else {
            eyeMoveDetector.tickEyeClosed();
        }


        if (imgView != null) {
            Bitmap bitmap = proxyConverter.createBitmap(outputFrame);
            imgView.setImageBitmap(bitmap);
        }
    }

    public void setImageView(ImageView imageView) {
        imgView = imageView;
    }

    public FPSCounter getFpsCounter() {
        return fpsCounter;
    }
}
