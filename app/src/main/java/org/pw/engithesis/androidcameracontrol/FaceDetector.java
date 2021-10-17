package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.facedetectionalgorithms.FaceDetectionAlgorithm;
import org.pw.engithesis.androidcameracontrol.facedetectionalgorithms.FaceDetectionHaar;

import java.util.ArrayList;
import java.util.Arrays;

public class FaceDetector {
    public static final double LEFT_BOUNDARY = 0.3;
    public static final double RIGHT_BOUNDARY = 0.7;
    private final FaceDetectionAlgorithm detectionAlgorithm;

    public FaceDetector() {
        // Haar Cascading Classifier is default eye detection algorithm
        this(new FaceDetectionHaar());
    }

    public FaceDetector(FaceDetectionAlgorithm detectionAlgorithm) {
        this.detectionAlgorithm = detectionAlgorithm;
    }

    public Rect detect(Mat frame) {
        Rect[] faces = detectionAlgorithm.detect(frame);

        return filterFaces(frame, faces);
    }


    private Rect filterFaces(Mat mat, Rect[] faces) {
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

        return getBiggestFaceRect(facesArrayList);
    }

    private boolean isInVerticalCenter(Mat frame, Rect face) {
        Point center = Utility.getCenterOfRect(face);

        int leftBoundaryX = (int) (frame.width() * LEFT_BOUNDARY);
        int rightBoundaryX = (int) (frame.width() * RIGHT_BOUNDARY);

        return (center.x >= leftBoundaryX && center.x <= rightBoundaryX);
    }

    private Rect getBiggestFaceRect(ArrayList<Rect> faces) {
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
