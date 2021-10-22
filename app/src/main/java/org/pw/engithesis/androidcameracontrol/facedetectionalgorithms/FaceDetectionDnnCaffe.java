package org.pw.engithesis.androidcameracontrol.facedetectionalgorithms;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.RawResourceManager;

import java.util.ArrayList;

public class FaceDetectionDnnCaffe implements FaceDetectionAlgorithm {
    private final Net dnnNet;

    public FaceDetectionDnnCaffe() {
        RawResourceManager proto = new RawResourceManager(R.raw.deploy, "deploy.protoxt");
        RawResourceManager model = new RawResourceManager(R.raw.res10_300x300_ssd_iter_140000_fp16, "res10_300x300_ssd_iter_140000_fp16.caffemodel");

        dnnNet = Dnn.readNetFromCaffe(proto.getPath(), model.getPath());
    }

    @Override
    public Rect[] detect(Mat frame) {
        Mat blob = Dnn.blobFromImage(frame, 1.0, new Size(300, 300), new Scalar(104.0, 177.0, 123.0), false, false);
        dnnNet.setInput(blob);
        Mat detections = dnnNet.forward();

        detections = detections.reshape(1, (int) detections.total() / 7);

        int cols = frame.cols();
        int rows = frame.rows();

        ArrayList<Rect> faces = new ArrayList<>();
        for (int i = 0; i < detections.rows(); i++) {
            double confidence = detections.get(i, 2)[0];

            if (confidence > 0.7) {
                int x1 = (int) (detections.get(i, 3)[0] * cols);
                int y1 = (int) (detections.get(i, 4)[0] * rows);
                int x2 = (int) (detections.get(i, 5)[0] * cols);
                int y2 = (int) (detections.get(i, 6)[0] * rows);

                int width = x2 - x1;
                int height = y2 - y1;

                faces.add(new Rect(x1, y1, width, height));
            }
        }

        return faces.toArray(new Rect[0]);
    }
}
