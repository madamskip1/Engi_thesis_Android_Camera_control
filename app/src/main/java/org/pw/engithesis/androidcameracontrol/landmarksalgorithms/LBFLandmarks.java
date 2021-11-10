package org.pw.engithesis.androidcameracontrol.landmarksalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.face.Face;
import org.opencv.face.Facemark;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.RawResourceManager;

import java.util.ArrayList;

public class LBFLandmarks implements LandmarksAlgorithm {
    private static final int LANDMARKS_NUM = 68;
    private final Facemark facemarkLBF;

    public LBFLandmarks() {
        facemarkLBF = Face.createFacemarkLBF();
        RawResourceManager rawResourceManager = new RawResourceManager(R.raw.lbfmodel, "lbfmodel.yaml");
        facemarkLBF.loadModel(rawResourceManager.getPath());
    }

    @Override
    public Point[] detect(Mat frame, Rect face) {
        MatOfRect matOfRectFace = new MatOfRect(face);
        ArrayList<MatOfPoint2f> landmarks = new ArrayList<>();
        facemarkLBF.fit(frame, matOfRectFace, landmarks);

        return arrayListToPointArray(landmarks);
    }

    private Point[] arrayListToPointArray(ArrayList<MatOfPoint2f> arrayList) {
        Point[] pointArray = new Point[LANDMARKS_NUM];
        MatOfPoint2f landmarks = arrayList.get(0);

        for (int i = 0; i < LANDMARKS_NUM; i++) {
            double[] dp = landmarks.get(i, 0);
            Point landmark = new Point(dp[0], dp[1]);
            pointArray[i] = landmark;
        }

        return pointArray;
    }
}
