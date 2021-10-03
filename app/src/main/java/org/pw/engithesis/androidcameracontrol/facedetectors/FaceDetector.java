package org.pw.engithesis.androidcameracontrol.facedetectors;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class FaceDetector {
    public static final double LEFT_BOUNDARY = 0.3;
    public static final double RIGHT_BOUNDARY = 0.7;

    public void drawFaceSquare(Mat mat, MatOfRect faces) {
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Rect face = facesArray[i];
            Imgproc.rectangle(mat, face.tl(), face.br(), new Scalar(255, 0, 0), 3);
        }

    }

    public abstract Rect[] detect(Mat mat);

    protected Rect[] filterFaces(Mat mat, Rect[] faces) {
        int facesNum = faces.length;

        if (facesNum == 0) {
            return null;
        }

        if (facesNum == 1) {
            if (isInVerticalCenter(mat, faces[0])) {
                return faces;
            }
            return null;
        }

        ArrayList<Rect> rectArrayList = new ArrayList<Rect>(Arrays.asList(faces));

        rectArrayList.removeIf(face -> !isInVerticalCenter(mat, face));


        return rectArrayList.toArray(new Rect[0]);
    }

    private boolean isInVerticalCenter(Mat mat, Rect face) {
        int centerX = face.x + (face.width / 2);
        int leftBoundaryX = (int) (mat.width() * LEFT_BOUNDARY);
        int rightBoundaryX = (int) (mat.width() * RIGHT_BOUNDARY);

        return (centerX >= leftBoundaryX && centerX <= rightBoundaryX);
    }

}
