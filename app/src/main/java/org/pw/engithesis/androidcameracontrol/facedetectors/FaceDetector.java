package org.pw.engithesis.androidcameracontrol.facedetectors;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public abstract class FaceDetector {
    public void drawFaceSquare(Mat mat, MatOfRect faces) {
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Rect face = facesArray[i];
            Imgproc.rectangle(mat, face.tl(), face.br(), new Scalar(255, 0, 0), 3);
        }

    }

    public abstract MatOfRect detect(Mat mat);
}
