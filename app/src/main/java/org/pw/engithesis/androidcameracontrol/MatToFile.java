package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class MatToFile {

    public void saveRGBMatAsPNG(String filename, Mat mat) {
        Mat bgrMat = mat.clone();
        Imgproc.cvtColor(mat, bgrMat, Imgproc.COLOR_RGB2BGR);

        saveAsPNG(filename, bgrMat);
    }

    public void saveGrayMatAsPng(String filename, Mat mat) {
        saveAsPNG(filename, mat);
    }

    private void saveAsPNG(String filename, Mat mat) {
        filename = filename + ".png";
        String path = App.getContext().getExternalFilesDir(null).getAbsolutePath();
        File imgDir = new File(path, "AndroidCameraControlMatIMG");

        if (!imgDir.exists()) {
            imgDir.mkdir();
        }
        File file = new File(imgDir, filename);
        filename = file.toString();

        Imgcodecs.imwrite(filename, mat);
    }
}
