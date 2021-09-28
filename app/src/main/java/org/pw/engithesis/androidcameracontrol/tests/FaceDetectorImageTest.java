package org.pw.engithesis.androidcameracontrol.tests;

import android.content.Context;
import android.widget.ScrollView;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.Utility;
import org.pw.engithesis.androidcameracontrol.ViewsBuilder;
import org.pw.engithesis.androidcameracontrol.facedetectors.HaarCascadeFaceDetector;

public class FaceDetectorImageTest extends ImageTest {
    private class FaceDetectorImageTestStruct {
        public int imgID;
        public Rect face;

        public FaceDetectorImageTestStruct(int id, Rect face) {
            imgID = id;
            this.face = face;
        }
    }

    private final FaceDetectorImageTestStruct[] imagesToTest = {
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_1, new Rect()),

    };

    private final HaarCascadeFaceDetector faceDetector;

    protected FaceDetectorImageTest(Context context, ScrollView parent) {
        super(context, parent);
        faceDetector = new HaarCascadeFaceDetector();
    }

    @Override
    public void createView() {
        ViewsBuilder viewsBuilder = new ViewsBuilder(ctx, parentView);

        for (int image = 0; image < imagesToTest.length; image++) {
            viewsBuilder.newSection();

            Mat imageMat = getImageMat(imagesToTest[image].imgID);
            MatOfRect matOfRect = faceDetector.detect(imageMat);
            Rect[] faces = matOfRect.toArray();
            Utility.drawRects(imageMat, faces);

            addImageToView(imageMat, viewsBuilder);
        }

        viewsBuilder.build();
    }
}
