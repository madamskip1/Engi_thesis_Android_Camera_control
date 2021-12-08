package org.pw.engithesis.androidcameracontrol.detectors.facemarksdetectionalgorithms;

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

public class FacemarksLBF implements FacemarksAlgorithm {
    private static final int FACEMARKS_NUM = 68;

    private final Facemark facemarkLBF;

    public FacemarksLBF() {
        facemarkLBF = Face.createFacemarkLBF();
        RawResourceManager rawResourceManager = new RawResourceManager(R.raw.lbfmodel, "lbfmodel.yaml");
        facemarkLBF.loadModel(rawResourceManager.getPath());
    }

    @Override
    public Point[] detect(Mat frame, Rect face) {
        MatOfRect matOfRectFace = new MatOfRect(face);
        ArrayList<MatOfPoint2f> facemarks = new ArrayList<>();
        facemarkLBF.fit(frame, matOfRectFace, facemarks);

        return arrayListToPointArray(facemarks);
    }

    private Point[] arrayListToPointArray(ArrayList<MatOfPoint2f> arrayList) {
        Point[] pointArray = new Point[FACEMARKS_NUM];
        MatOfPoint2f facemarks = arrayList.get(0);

        for (int i = 0; i < FACEMARKS_NUM; i++) {
            double[] dp = facemarks.get(i, 0);
            Point landmark = new Point(dp[0], dp[1]);
            pointArray[i] = landmark;
        }

        return pointArray;
    }
}
