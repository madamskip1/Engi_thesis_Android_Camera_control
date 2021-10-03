package org.pw.engithesis.androidcameracontrol.tests;

import android.content.Context;
import android.widget.ScrollView;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.Utility;
import org.pw.engithesis.androidcameracontrol.ViewsBuilder;
import org.pw.engithesis.androidcameracontrol.facedetectors.HaarCascadeFaceDetector;

public class FaceDetectorImageTest extends ImageTest {
    private static class FaceDetectorImageTestStruct {
        public int imgID;
        public Rect minFaceRect;
        public Rect maxFaceRect;

        public FaceDetectorImageTestStruct(int id, Rect minFaceRect, Rect maxFaceRect) {
            imgID = id;
            this.minFaceRect = minFaceRect;
            this.maxFaceRect = maxFaceRect;
        }
    }

    private final FaceDetectorImageTestStruct[] imagesToTest = {
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_1, new Rect(285, 120, 310, 290), new Rect(230, 15, 415, 500)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_2, new Rect(145, 290, 320, 390), new Rect(75, 135, 450, 660)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_3, new Rect(250, 110, 94, 94), new Rect(234, 75, 130, 150)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_4, new Rect(190, 75, 85, 110), new Rect(175, 50, 130, 155)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_5, new Rect(205, 105, 220, 230), new Rect(150, 30, 320, 370)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_6, new Rect(140, 160, 300, 310), new Rect(80, 27, 430, 510)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_7, new Rect(250, 155, 115, 130), new Rect(217, 110, 180, 210)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_8, new Rect(695, 275, 275, 300), new Rect(640, 140, 390, 500)),
            new FaceDetectorImageTestStruct(R.drawable.portrait_test_9, new Rect(210, 265, 295, 355), new Rect(180, 145, 425, 570)),
    };

    private final HaarCascadeFaceDetector faceDetector;

    protected FaceDetectorImageTest(Context context, ScrollView parent) {
        super(context, parent);
        faceDetector = new HaarCascadeFaceDetector();
    }

    @Override
    public void createView() {
        ViewsBuilder viewsBuilder = new ViewsBuilder(ctx, parentView);

        for (FaceDetectorImageTestStruct imageToTest : imagesToTest) {
            viewsBuilder.newSection();

            Mat imageMat = getImageMat(imageToTest.imgID);
            Rect[] faces = faceDetector.detect(imageMat);
            Utility.drawRects(imageMat, faces);
            Utility.drawRects(imageMat, new Rect[]{imageToTest.minFaceRect, imageToTest.maxFaceRect}, new Scalar(60, 130, 255));

            addImageToView(imageMat, viewsBuilder);
        }

        viewsBuilder.build();
    }
}
