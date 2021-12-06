package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.pw.engithesis.androidcameracontrol.interfaces.Observable;

import java.util.ArrayList;

public class EyeMoveDetector extends Observable {
    private static final int CONCUSSIVE_FRAME_TO_MOVE = 3;
    private static final double SIDE_POS_BORDER = 0.40;

    private final ArrayList<EYE_POS> eyePupilPosPrevFrames = new ArrayList<>(); // [0.0, 1.0] of X-Axis
    private EYE_POS lastPos = EYE_POS.CLOSED;
    private EYE_MOVE eyeMovedTo = EYE_MOVE.NONE;

    public EyeMoveDetector() {
        eyePupilPosPrevFrames.ensureCapacity(CONCUSSIVE_FRAME_TO_MOVE);
        for (int i = 0; i < CONCUSSIVE_FRAME_TO_MOVE; i++) {
            eyePupilPosPrevFrames.add(null);
        }
    }

    public EYE_MOVE tickEyesOpen(Point[] eyesPupil, Rect[] eyesRect) {
        eyeMovedTo = EYE_MOVE.NONE;
        EYE_POS eyePos = calcEyePupilPos(eyesPupil[0], eyesRect[0], eyesPupil[1], eyesRect[1]);
        addToPrevFramesList(eyePos);

        if (lastPos == eyePos) {
            return EYE_MOVE.NONE;
        }

        if (lastPos == EYE_POS.CLOSED) {
            if (checkIfMovedTo(EYE_POS.LEFT)) {
                openEyes(EYE_POS.LEFT);
            } else if (checkIfMovedTo(EYE_POS.RIGHT)) {
                openEyes(EYE_POS.RIGHT);
            } else if (checkIfMovedTo(EYE_POS.CENTER)) {
                openEyes(EYE_POS.CENTER);
            } // else -> even if positions in all previous frames were OPEN we can keep lastPos == CLOSED, because all weren't same pos in a row (in CONCUSSIVE_FRAME_TO_MOVE)
        } else if (lastPos == EYE_POS.CENTER) {
            tryToMoveFromCenter();
        } else if (lastPos == EYE_POS.LEFT) {
            tryToMoveFromLeft();
        } else if (lastPos == EYE_POS.RIGHT) {
            tryToMoveFromRight();
        }

        if (eyeMovedTo != EYE_MOVE.NONE) {
            notifyUpdate();
        }

        return eyeMovedTo;
    }

    public void tickEyeClosed() {
        eyeMovedTo = EYE_MOVE.NONE;
        addToPrevFramesList(EYE_POS.CLOSED);
        if (lastPos != EYE_POS.CLOSED && checkIfMovedTo(EYE_POS.CLOSED)) {
            closeEyes();
        }
    }

    public EYE_MOVE getEyeMovedTo() {
        return eyeMovedTo;
    }

    private EYE_POS calcEyePupilPos(Point leftEyePupil, Rect leftEyeRect, Point rightEyePupil, Rect rightEyeRect) {
        double leftEyePos = (leftEyePupil.x - leftEyeRect.x) / (double) leftEyeRect.width;
        double rightEyePos = (rightEyePupil.x - rightEyeRect.x) / (double) rightEyeRect.width;

        double avgPos = (leftEyePos + rightEyePos) / 2.0;

        return xAxisPosToEYE_POS(avgPos);
    }

    private EYE_POS xAxisPosToEYE_POS(Double xPos) {
        if (xPos == null) {
            return EYE_POS.CLOSED;
        } else if (xPos < SIDE_POS_BORDER) {
            return EYE_POS.LEFT;
        } else if (xPos > (1.0 - SIDE_POS_BORDER)) {
            return EYE_POS.RIGHT;
        } else {
            return EYE_POS.CENTER;
        }
    }

    private void addToPrevFramesList(EYE_POS pos) {
        eyePupilPosPrevFrames.remove(0);
        eyePupilPosPrevFrames.add(pos);
    }


    private boolean checkIfMovedTo(EYE_POS expectedPos) {
        for (int i = 0; i < CONCUSSIVE_FRAME_TO_MOVE; i++) {
            if (expectedPos != eyePupilPosPrevFrames.get(i)) {
                return false;
            }
        }

        return true;
    }

    private void tryToMoveFromLeft() {
        if (checkIfMovedTo(EYE_POS.CENTER)) {
            movedCenter();
        } else if (checkIfMovedTo(EYE_POS.RIGHT)) {
            movedRight();
        }
    }

    private void tryToMoveFromCenter() {
        if (checkIfMovedTo(EYE_POS.LEFT)) {
            movedLeft();
        } else if (checkIfMovedTo(EYE_POS.RIGHT)) {
            movedRight();
        }
    }

    private void tryToMoveFromRight() {
        if (checkIfMovedTo(EYE_POS.LEFT)) {
            movedLeft();
        } else if (checkIfMovedTo(EYE_POS.CENTER)) {
            movedCenter();
        }
    }

    private void movedLeft() {
        lastPos = EYE_POS.LEFT;
        eyeMovedTo = EYE_MOVE.LEFT;
    }

    private void movedCenter() {
        lastPos = EYE_POS.CENTER;
        eyeMovedTo = EYE_MOVE.CENTER;
    }

    private void movedRight() {
        lastPos = EYE_POS.RIGHT;
        eyeMovedTo = EYE_MOVE.RIGHT;
    }

    private void closeEyes() {
        lastPos = EYE_POS.CLOSED;
    }

    private void openEyes(EYE_POS openInPos) {
        lastPos = openInPos;
        // don't do move just after eyes open
    }

    private enum EYE_POS {LEFT, CENTER, RIGHT, CLOSED}

    public enum EYE_MOVE {LEFT, CENTER, RIGHT, NONE}
}
