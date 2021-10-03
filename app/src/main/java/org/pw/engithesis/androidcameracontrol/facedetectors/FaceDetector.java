package org.pw.engithesis.androidcameracontrol.facedetectors;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class FaceDetector {
    public static final double LEFT_BOUNDARY = 0.3;
    public static final double RIGHT_BOUNDARY = 0.7;

    public abstract Rect detect(Mat mat);

    protected Rect filterFaces(Mat mat, Rect[] faces) {
        if (faces.length == 0) {
            return null;
        }

        ArrayList<Rect> facesArrayList = new ArrayList<>(Arrays.asList(faces));
        facesArrayList.removeIf(face -> !isInVerticalCenter(mat, face));

        int facesNum = facesArrayList.size();

        if (facesNum == 0) {
            return null;
        } else if (facesNum == 1) {
            return facesArrayList.get(0);
        }

        return chooseBiggestFaceRect(facesArrayList);
    }

    private boolean isInVerticalCenter(Mat mat, Rect face) {
        int centerX = face.x + (face.width / 2);
        int leftBoundaryX = (int) (mat.width() * LEFT_BOUNDARY);
        int rightBoundaryX = (int) (mat.width() * RIGHT_BOUNDARY);

        return (centerX >= leftBoundaryX && centerX <= rightBoundaryX);
    }

    private Rect chooseBiggestFaceRect(ArrayList<Rect> faces) {
        Rect biggestRect = faces.get(0);
        double biggestRectSize = biggestRect.width * biggestRect.height;

        for (int i = 1; i < faces.size(); i++) {
            Rect curRect = faces.get(i);
            double curSize = curRect.width * curRect.height;

            if (curSize > biggestRectSize) {
                biggestRect = curRect;
                biggestRectSize = curSize;
            }
        }

        return biggestRect;
    }


}
