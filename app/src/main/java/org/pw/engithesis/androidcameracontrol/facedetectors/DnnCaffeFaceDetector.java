package org.pw.engithesis.androidcameracontrol.facedetectors;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.RawResourceManager;

import java.util.ArrayList;

public class DnnCaffeFaceDetector extends FaceDetector {
    private final Net dnnNet;

    public DnnCaffeFaceDetector()
    {
        RawResourceManager proto = new RawResourceManager(R.raw.deploy, "deploy.protoxt");
        RawResourceManager model = new RawResourceManager(R.raw.res10_300x300_ssd_iter_140000_fp16, "res10_300x300_ssd_iter_140000_fp16.caffemodel");

        dnnNet = Dnn.readNetFromCaffe(proto.getPath(), model.getPath());
    }

    @Override
    public Rect[] detect(Mat mat) {
        Mat blob = Dnn.blobFromImage(mat, 1.0, new Size(300, 300), new Scalar(104.0, 177.0, 123.0), false, false);
        dnnNet.setInput(blob);
        Mat detections = dnnNet.forward();

        detections = detections.reshape(1, (int) detections.total() / 7);

        int cols = mat.cols();
        int rows = mat.rows();

        ArrayList<Rect> faces = new ArrayList<Rect>();

        Log.d("DnnCaffe", "probably detected:" + detections.rows());

        for (int i = 0; i < detections.rows(); i++)
        {
            double confidence = detections.get(i, 2)[0];

            if (confidence > 0.7)
            {
                int x1 = (int) (detections.get(i, 3)[0] * cols);
                int y1 = (int) (detections.get(i, 4)[0] * rows);
                int x2 = (int) (detections.get(i, 5)[0] * cols);
                int y2 = (int) (detections.get(i, 6)[0] * rows);
                Log.d("dnnCaffe", "detected i = " + i + " " + cols + "x" + rows + " -> " + x1 + " " + y1 + ", " + x2 + " " + y2);

                faces.add(new Rect(x1, y1, x2, y2));
                //Imgproc.rectangle(mat, new Point(x1, y1), new Point(x2, y2),  new Scalar(0, 255, 0),2, 4);
            }
        }

        Rect[] facesRectArray = (Rect[]) faces.toArray();
        return facesRectArray;
    }
}
