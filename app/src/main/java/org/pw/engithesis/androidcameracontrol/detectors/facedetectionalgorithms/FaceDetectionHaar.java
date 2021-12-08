package org.pw.engithesis.androidcameracontrol.detectors.facedetectionalgorithms;

import org.pw.engithesis.androidcameracontrol.R;

public class FaceDetectionHaar extends FaceDetectionCascade {
    public FaceDetectionHaar() {
        super(R.raw.haarcascade_frontalface_default, "haarcascade_frontalface_default.xml");
    }
}
