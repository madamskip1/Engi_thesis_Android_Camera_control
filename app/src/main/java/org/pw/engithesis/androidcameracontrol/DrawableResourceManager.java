package org.pw.engithesis.androidcameracontrol;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class DrawableResourceManager {
    private int _resourceID;
    private Mat BGRMat = null;
    private Mat RGBMat = null;

    public DrawableResourceManager(int resourceID)
    {
        _resourceID = resourceID;
    }

    public Mat getBGRMat()
    {
        if (BGRMat == null)
        {
            try
            {
                BGRMat = Utils.loadResource(App.getContext(), _resourceID, CvType.CV_8UC4);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return BGRMat;
    }

    public Mat getRGBMat()
    {
        if (RGBMat == null)
        {
            RGBMat = new Mat();
            Imgproc.cvtColor(getBGRMat(), RGBMat, Imgproc.COLOR_BGR2RGB);
        }
        return RGBMat;
    }
}
