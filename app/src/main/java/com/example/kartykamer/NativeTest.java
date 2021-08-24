package com.example.kartykamer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.Collections;
import java.util.List;

public class NativeTest extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase mOpenCvBackCamerView;
    private int mCameraId = 1;

    static {
        System.loadLibrary("kartykamer");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS)
            {
                mOpenCvBackCamerView.enableView();
            }
            else
            {
                super.onManagerConnected(status);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_test);

        TextView textView = findViewById(R.id.sample_text);
        textView.setText(testString());


        mOpenCvBackCamerView = (CameraBridgeViewBase) findViewById(R.id.nativeCamera);
        mOpenCvBackCamerView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        //mOpenCvBackCamerView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
        mOpenCvBackCamerView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvBackCamerView.setCvCameraViewListener(this);
    }

    public native  String testString();

    public native void testMat(long mat);

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvBackCamerView != null)
        {
            mOpenCvBackCamerView.disableView();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug())
        {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        }
        else
        {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvBackCamerView);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mOpenCvBackCamerView != null)
        {
            mOpenCvBackCamerView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat test = inputFrame.gray();
        testMat(test.getNativeObjAddr());

        return test;
    }
}