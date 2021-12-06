package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.ArrayList;

public class EyeMoveDetector {
    private static final int CONCUSSIVE_FRAME_TO_MOVE = 3;
    private static final double SIDE_POS_BORDER = 0.40;

    private final ArrayList<EYE_POS> eyePupilPosPrevFrames = new ArrayList<>(); // [0.0, 1.0] of X-Axis
    private EYE_POS lastPos = EYE_POS.CLOSED;

    public EyeMoveDetector() {
        eyePupilPosPrevFrames.ensureCapacity(CONCUSSIVE_FRAME_TO_MOVE);
        for (int i = 0; i < CONCUSSIVE_FRAME_TO_MOVE; i++) {
            eyePupilPosPrevFrames.add(null);
        }
    }

    public EYE_MOVE tickEyesOpen(Point[] eyesPupil, Rect[] eyesRect) {
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

            return EYE_MOVE.NONE; // don't do move just after eyes open
        }
        if (lastPos == EYE_POS.CENTER) {
            return tryToMoveFromCenter();
        }
        if (lastPos == EYE_POS.LEFT) {
            return tryToMoveFromLeft();
        }
        // else lastPos == EYE_POS.RIGHT
        return tryToMoveFromRight();
    }

    public void tickEyeClosed() {
        addToPrevFramesList(EYE_POS.CLOSED);
        if (lastPos != EYE_POS.CLOSED && checkIfMovedTo(EYE_POS.CLOSED)) {
            closeEyes();
        }
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

    private EYE_MOVE tryToMoveFromLeft() {
        if (checkIfMovedTo(EYE_POS.CENTER)) {
            lastPos = EYE_POS.CENTER;
            return EYE_MOVE.CENTER;
        }
        if (checkIfMovedTo(EYE_POS.RIGHT)) {
            lastPos = EYE_POS.RIGHT;
            return EYE_MOVE.RIGHT;
        }

        return EYE_MOVE.NONE;
    }

    private EYE_MOVE tryToMoveFromCenter() {
        if (checkIfMovedTo(EYE_POS.LEFT)) {
            lastPos = EYE_POS.LEFT;
            return EYE_MOVE.LEFT;
        }
        if (checkIfMovedTo(EYE_POS.RIGHT)) {
            lastPos = EYE_POS.RIGHT;
            return EYE_MOVE.RIGHT;
        }

        return EYE_MOVE.NONE;
    }

    private EYE_MOVE tryToMoveFromRight() {
        if (checkIfMovedTo(EYE_POS.LEFT)) {
            lastPos = EYE_POS.LEFT;
            return EYE_MOVE.LEFT;
        }
        if (checkIfMovedTo(EYE_POS.CENTER)) {
            lastPos = EYE_POS.CENTER;
            return EYE_MOVE.CENTER;
        }

        return EYE_MOVE.NONE;
    }

    private void closeEyes() {
        lastPos = EYE_POS.CLOSED;
    }

    private void openEyes(EYE_POS openInPos) {
        lastPos = openInPos;
    }

    private enum EYE_POS {LEFT, CENTER, RIGHT, CLOSED}

    public enum EYE_MOVE {LEFT, CENTER, RIGHT, NONE}
}
