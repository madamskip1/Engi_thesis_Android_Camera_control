package org.pw.engithesis.androidcameracontrol.tests;

import android.content.Context;
import android.widget.ScrollView;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.EyeBlinkDetector;
import org.pw.engithesis.androidcameracontrol.FaceDetector;
import org.pw.engithesis.androidcameracontrol.LandmarksDetector;
import org.pw.engithesis.androidcameracontrol.MatToFile;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.Utility;
import org.pw.engithesis.androidcameracontrol.ViewsBuilder;
import org.pw.engithesis.androidcameracontrol.facedetectionalgorithms.FaceDetectionDnnCaffe;
import org.pw.engithesis.androidcameracontrol.landmarksalgorithms.DlibLandmarks;

public class EARImageTest extends ImageTest {
    private final EARImageTestStruct[] imagesToTest = {
            new EARImageTestStruct(R.drawable.portrait_test_1, new Boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.portrait_test_2, new Boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.portrait_test_3, new Boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.portrait_test_4, new Boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.portrait_test_5, new Boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.portrait_test_6, new Boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.portrait_test_7, new Boolean[]{true, true}),
            new EARImageTestStruct(R.drawable.portrait_test_8, new Boolean[]{false, true}),
            new EARImageTestStruct(R.drawable.portrait_test_9, new Boolean[]{false, true})
    };
    private final FaceDetector faceDetector;
    private final LandmarksDetector landmarksDetector;
    private final EyeBlinkDetector blinkDetector;

    public EARImageTest(Context ctx, ScrollView parent) {
        super(ctx, parent);
        faceDetector = new FaceDetector(new FaceDetectionDnnCaffe());
        landmarksDetector = new LandmarksDetector(new DlibLandmarks());
        blinkDetector = new EyeBlinkDetector();
    }

    @Override
    public void createView() {
        ViewsBuilder viewsBuilder = new ViewsBuilder(ctx, parentView);

        MatToFile matToFile = new MatToFile();

        int i = 0;
        for (EARImageTestStruct image : imagesToTest) {
            viewsBuilder.newSection();


            Mat imageMat = getImageMat(image.imgID);
            Mat outputMat = imageMat.clone();

            Rect face = faceDetector.detect(imageMat);
            Utility.drawRects(outputMat, new Rect[]{face});

            Point[] landmarks = landmarksDetector.detect(imageMat, face);
            landmarksDetector.drawLandmarks(outputMat);

            blinkDetector.checkEyeBlink(landmarksDetector.getRightEyeLandmarks(), landmarksDetector.getLeftEyeLandmarks());

            addImageToView(outputMat, viewsBuilder);
            viewsBuilder.addText("right: " + blinkDetector.rightEyeEAR);
            viewsBuilder.addText("left: " + blinkDetector.leftEyeEAR);

            //matToFile.saveRGBMatAsPNG("EAR" + i, outputMat);
            i++;
        }

        viewsBuilder.build();
    }

    private static class EARImageTestStruct {
        public int imgID;
        public Boolean[] eyeOpen;

        public EARImageTestStruct(int id, Boolean[] open) {
            imgID = id;
            eyeOpen = open;
        }
    }
}
