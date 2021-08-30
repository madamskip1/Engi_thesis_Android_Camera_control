package com.example.kartykamer.facedetectors;

import com.example.kartykamer.R;

public class HaarCascadeFaceDetector extends CascadeFaceDetector {
    public HaarCascadeFaceDetector() {
        super(R.raw.haarcascade_frontalface_default, "haarcascade_frontalface_default.xml");
    }
}
