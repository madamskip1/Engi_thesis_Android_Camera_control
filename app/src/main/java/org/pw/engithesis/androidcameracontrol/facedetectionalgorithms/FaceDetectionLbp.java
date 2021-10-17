package org.pw.engithesis.androidcameracontrol.facedetectionalgorithms;

import org.pw.engithesis.androidcameracontrol.R;

public class FaceDetectionLbp extends FaceDetectionCascade {
    public FaceDetectionLbp() {
        super(R.raw.lbpcascade_frontalface, "lbpcascade_frontalface.xml");
    }
}
