package org.pw.engithesis.androidcameracontrol.tests;

import android.content.Context;
import android.widget.ScrollView;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.EyeBlinkDetector;
import org.pw.engithesis.androidcameracontrol.FacemarkDetector;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.Utility;
import org.pw.engithesis.androidcameracontrol.ViewsBuilder;
import org.pw.engithesis.androidcameracontrol.facedetectors.HaarCascadeFaceDetector;

import java.util.ArrayList;

public class EARImageTest extends ImageTest {
    private class EARImageTestStruct {
        public int imgID;
        public Boolean[] eyeOpen;

        public EARImageTestStruct(int id, Boolean[] open) {
            imgID = id;
            eyeOpen = open;
        }
    }

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

    private final HaarCascadeFaceDetector faceDetector;
    private final FacemarkDetector facemarkDetector;
    private final EyeBlinkDetector blinkDetector;

    public EARImageTest(Context ctx, ScrollView parent) {
        super(ctx, parent);
        faceDetector = new HaarCascadeFaceDetector();
        facemarkDetector = new FacemarkDetector();
        blinkDetector = new EyeBlinkDetector();
    }

    @Override
    public void createView() {
        ViewsBuilder viewsBuilder = new ViewsBuilder(ctx, parentView);

        for (int image = 0; image < imagesToTest.length; image++) {
            viewsBuilder.newSection();

            EARImageTestStruct imageTestStruct = imagesToTest[image];

            Mat imageMat = getImageMat(imageTestStruct.imgID);
            Mat outputMat = imageMat.clone();

            Rect[] faces = faceDetector.detect(imageMat);
            Utility.drawRects(outputMat, faces);

            ArrayList<MatOfPoint2f> landmarks = facemarkDetector.detect(imageMat, new MatOfRect(faces));
            facemarkDetector.drawLandmarks(outputMat, landmarks);

            blinkDetector.checkEyeBlink(landmarks.get(0));

            addImageToView(outputMat, viewsBuilder);
            viewsBuilder.addText("ladmarks " + landmarks.size());
            viewsBuilder.addText("right: " + blinkDetector.rightEyeEAR);
            viewsBuilder.addText("left: " + blinkDetector.leftEyeEAR);
        }

        viewsBuilder.build();
    }
}
