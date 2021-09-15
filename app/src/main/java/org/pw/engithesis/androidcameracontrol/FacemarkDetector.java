package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.face.Face;
import org.opencv.face.Facemark;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class FacemarkDetector {
    private Facemark facemarkLBF;

    public FacemarkDetector()
    {
        facemarkLBF = Face.createFacemarkLBF();
        RawResourceManager rawResourceManager = new RawResourceManager(R.raw.lbfmodel, "lbfmodel.yaml");
        facemarkLBF.loadModel(rawResourceManager.getPath());
    }

    public ArrayList<MatOfPoint2f> detect(Mat mat, MatOfRect faces)
    {
        ArrayList<MatOfPoint2f> landmarks = new ArrayList<>();
        facemarkLBF.fit(mat, faces, landmarks);

        return landmarks;
    }

    public void drawLandmarks(Mat mat, ArrayList<MatOfPoint2f> landmarks)
    {
        for (int i = 0; i < landmarks.size(); i++) {
            MatOfPoint2f lm = landmarks.get(i);

            for (int j = 0; j < lm.rows(); j++) {
                double[] dp = lm.get(j, 0);
                Point p = new Point(dp[0], dp[1]);
                Imgproc.circle(mat, p, 2, new Scalar(0, 200, 0), 1);
            }
        }
    }
}
