package org.pw.engithesis.androidcameracontrol.tests;

import android.content.Context;
import android.widget.ScrollView;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.pw.engithesis.androidcameracontrol.EyeDetector;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.Utility;
import org.pw.engithesis.androidcameracontrol.ViewsBuilder;
import org.pw.engithesis.androidcameracontrol.facedetectors.HaarCascadeFaceDetector;

public class EyeDetectorImageTest extends ImageTest {
    private static class EyesImageTestStruct {
        public int imgID;
        public SingleEyeTestStruct[] eyes;

        public EyesImageTestStruct(int id, SingleEyeTestStruct[] eyes) {
            imgID = id;
            this.eyes = eyes;
        }
    }

    private static class SingleEyeTestStruct {
        public Rect minEyeRect;
        public Rect maxEyeRect;

        public SingleEyeTestStruct(Rect minEyeRect, Rect maxEyeRect) {
            this.minEyeRect = minEyeRect;
            this.maxEyeRect = maxEyeRect;
        }
    }

    private final HaarCascadeFaceDetector faceDetector;
    private final EyeDetector eyeDetector;

    private final EyesImageTestStruct[] imagesToTest = {
            new EyesImageTestStruct(R.drawable.portrait_test_1, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(318, 202, 78, 46), new Rect(286, 166, 130, 100)),
                    new SingleEyeTestStruct(new Rect(489, 199, 72, 41), new Rect(452, 158, 148, 101))}),
            new EyesImageTestStruct(R.drawable.portrait_test_2, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(186, 396, 63, 36), new Rect(153, 356, 121, 101)),
                    new SingleEyeTestStruct(new Rect(343, 401, 64, 36), new Rect(313, 356, 143, 110))}),
            new EyesImageTestStruct(R.drawable.portrait_test_3, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(262, 130, 20, 11), new Rect(250, 115, 41, 32)),
                    new SingleEyeTestStruct(new Rect(310, 131, 19, 9), new Rect(300, 115, 41, 34))}),
            new EyesImageTestStruct(R.drawable.portrait_test_4, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(193, 113, 18, 9), new Rect(184, 91, 38, 37)),
                    new SingleEyeTestStruct(new Rect(240, 108, 18, 9), new Rect(228, 88, 44, 38))}),
            new EyesImageTestStruct(R.drawable.portrait_test_5, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(234, 169, 42, 19), new Rect(205, 141, 92, 72)),
                    new SingleEyeTestStruct(new Rect(343, 177, 41, 19), new Rect(326, 149, 93, 69))}),
            new EyesImageTestStruct(R.drawable.portrait_test_6, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(175, 250, 59, 29), new Rect(132, 209, 142, 99)),
                    new SingleEyeTestStruct(new Rect(344, 246, 57, 28), new Rect(304, 201, 129, 102))}),
            new EyesImageTestStruct(R.drawable.portrait_test_7, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(267, 194, 22, 10), new Rect(253, 172, 48, 44)),
                    new SingleEyeTestStruct(new Rect(327, 197, 23, 10), new Rect(314, 173, 50, 45))}),
            new EyesImageTestStruct(R.drawable.portrait_test_8, new SingleEyeTestStruct[]{
                    null,
                    new SingleEyeTestStruct(new Rect(884, 340, 47, 26), new Rect(856, 296, 106, 101))}),
            new EyesImageTestStruct(R.drawable.portrait_test_9, new SingleEyeTestStruct[]{
                    new SingleEyeTestStruct(new Rect(250, 382, 47, 26), new Rect(210, 325, 115, 118)),
                    null})
    };

    protected EyeDetectorImageTest(Context context, ScrollView parent) {
        super(context, parent);
        faceDetector = new HaarCascadeFaceDetector();
        eyeDetector = new EyeDetector();
    }

    @Override
    public void createView() {
        ViewsBuilder viewsBuilder = new ViewsBuilder(ctx, parentView);

        for (EyesImageTestStruct image : imagesToTest) {
            viewsBuilder.newSection();

            Mat imageMat = getImageMat(image.imgID);
            Mat outputMat = imageMat.clone();
            Rect face = faceDetector.detect(imageMat);


            if (face != null) {
                Rect[] eyes = eyeDetector.detect(imageMat, face);
                Utility.drawRects(outputMat, eyes);
            }

            for (SingleEyeTestStruct expectedEye : image.eyes) {
                if (expectedEye != null) {
                    Utility.drawRects(outputMat, new Rect[]{expectedEye.minEyeRect, expectedEye.maxEyeRect}, new Scalar(60, 130, 255));
                }
            }

            addImageToView(outputMat, viewsBuilder);
        }

        viewsBuilder.build();
    }
}
