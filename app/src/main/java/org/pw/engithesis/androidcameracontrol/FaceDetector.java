package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.facedetectionalgorithms.FaceDetectionAlgorithm;
import org.pw.engithesis.androidcameracontrol.facedetectionalgorithms.FaceDetectionDlibHOG;

import java.util.ArrayList;
import java.util.Arrays;

public class FaceDetector {
    public static final double LEFT_BOUNDARY = 0.3;
    public static final double RIGHT_BOUNDARY = 0.7;
    private final FaceDetectionAlgorithm detectionAlgorithm;

    public FaceDetector() {
        // Dlib HOG is default face detection algorithm
        // was best in comparision
        this(new FaceDetectionDlibHOG());
    }

    public FaceDetector(FaceDetectionAlgorithm detectionAlgorithm) {
        this.detectionAlgorithm = detectionAlgorithm;
    }

    public Rect detect(Mat frame) {
        Rect[] faces = detectionAlgorithm.detect(frame);

        Rect face = filterFaces(frame, faces);
        if (face == null) {
            return null;
        }

        return clipFaceRectToFrame(frame, face);
    }


    private Rect filterFaces(Mat mat, Rect[] faces) {
        if (faces.length == 0) {
            return null;
        }

        int verticalAdditionalBound = (int) (mat.height() * 0.1);
        int horizontalAdditionalBound = (int) (mat.width() * 0.1);
        int leftBound = -horizontalAdditionalBound;
        int topBound = -verticalAdditionalBound;
        int rightBound = mat.width() + horizontalAdditionalBound;
        int bottomBound = mat.height() + verticalAdditionalBound;

        ArrayList<Rect> facesArrayList = new ArrayList<>(Arrays.asList(faces));
        facesArrayList.removeIf(face -> !isInVerticalCenter(mat, face));
        facesArrayList.removeIf(face -> isOutOfFrame(face, leftBound, topBound, rightBound, bottomBound));

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

    private boolean isOutOfFrame(Rect face, int leftBound, int topBound, int rightBound, int bottomBound) {
        int x2 = face.x + face.width;
        int y2 = face.y + face.height;

        return (face.x < leftBound || face.y < topBound || x2 > rightBound || y2 > bottomBound);
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

    private Rect clipFaceRectToFrame(Mat frame, Rect face) {
        if (face.x < 0) {
            int diff = -face.x;
            face.x = 0;
            face.width -= diff;
        }
        if (face.y < 0) {
            int diff = -face.y;
            face.y = 0;
            face.height -= diff;
        }

        int maxX = face.x + face.width;
        if (maxX > frame.width()) {
            int diff = maxX - frame.width();
            face.width -= diff;
        }
        int maxY = face.y + face.height;
        if (maxY > frame.height()) {
            int diff = maxY - frame.height();
            face.height -= diff;
        }

        return face;
    }
}
