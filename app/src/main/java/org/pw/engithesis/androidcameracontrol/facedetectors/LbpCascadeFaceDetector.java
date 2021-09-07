package org.pw.engithesis.androidcameracontrol.facedetectors;

import org.pw.engithesis.androidcameracontrol.R;

public class LbpCascadeFaceDetector extends CascadeFaceDetector {
    public LbpCascadeFaceDetector() {
        super(R.raw.lbpcascade_frontalface, "lbpcascade_frontalface.xml");
    }
}
