package org.pw.engithesis.androidcameracontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageProxy;

public abstract class ImageAnalyser {
    protected final ImageProxyToMatConverter proxyConverter = new ImageProxyToMatConverter();
    private final CameraFramesProvider cameraFramesProvider;

    protected ImageAnalyser(AppCompatActivity activity) {
        cameraFramesProvider = new CameraFramesProvider(activity, this);
    }

    abstract void analyse(ImageProxy image);

    public void start() {
        cameraFramesProvider.start();
    }
}
