package com.example.kartykamer.facedetectors;

import com.example.kartykamer.R;

public class LbpCascadeFaceDetector extends CascadeFaceDetector {
    public LbpCascadeFaceDetector() {
        super(R.raw.lbpcascade_frontalface, "lbpcascade_frontalface.xml");
    }
}
