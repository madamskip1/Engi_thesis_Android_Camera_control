package org.pw.engithesis.androidcameracontrol.facedetectors;

import org.pw.engithesis.androidcameracontrol.R;

public class HaarCascadeFaceDetector extends CascadeFaceDetector {
    public HaarCascadeFaceDetector() {
        super(R.raw.haarcascade_frontalface_default, "haarcascade_frontalface_default.xml");
    }
}
