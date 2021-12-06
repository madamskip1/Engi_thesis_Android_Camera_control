package org.pw.engithesis.androidcameracontrol;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.ArrayList;

public class EyeMoveDetector {
    private static final int PREVIOUS_FRAMES_LIST_SIZE = 3;
    private static final double SIDE_POS_BORDER = 0.35;
    private final ArrayList<EYE_POS> eyePupilPosPrevFrames = new ArrayList<>(); // [0.0, 1.0] of X-Axis
    private boolean canMoveLeft = false;
    private boolean canMoveRight = false;
    private boolean areEyesClosed = true;
    private EYE_POS lastPos = EYE_POS.CLOSED;

    public EyeMoveDetector() {
        eyePupilPosPrevFrames.ensureCapacity(PREVIOUS_FRAMES_LIST_SIZE);
        for (int i = 0; i < PREVIOUS_FRAMES_LIST_SIZE; i++) {
            eyePupilPosPrevFrames.add(null);
        }
    }

    public EYE_MOVE tickEyesOpen(Point[] eyesPupil, Rect[] eyesRect) {
        EYE_POS eyePos = calcEyePupilPos(eyesPupil[0], eyesRect[0], eyesPupil[1], eyesRect[1]);
        addToPrevFramesList(eyePos);

        if (lastPos == eyePos) {
            return EYE_MOVE.NONE;
        }

        if (lastPos == EYE_POS.CLOSED)   // (3) przyszlo otwarte, bylo zamkniete
        {
            if (checkIfMovedTo(EYE_POS.LEFT)) {
                openEyes(EYE_POS.LEFT);
            }
            if (checkIfMovedTo(EYE_POS.RIGHT)) {
                openEyes(EYE_POS.RIGHT);
            }
            if (checkIfMovedTo(EYE_POS.CENTER)) {
                openEyes(EYE_POS.CENTER);
            }
            // even if positions in all previous frames were OPEN we can keep lastPos == CLOSED
        } else if (lastPos == EYE_POS.CENTER) // (4) przyjdzie otwarte było centrum
        {
            if (checkIfMovedTo(EYE_POS.LEFT)) {
                movedLeft();
                return EYE_MOVE.LEFT;
            }
            if (checkIfMovedTo(EYE_POS.RIGHT)) {
                movedRight();
                return EYE_MOVE.RIGHT;
            }
        } else if (lastPos == EYE_POS.LEFT)  // (5) przyjdzie otwarte, było lewo
        {
            if (checkIfMovedTo(EYE_POS.CENTER)) {
                movedCenter();
                return EYE_MOVE.CENTER;
            }
            if (checkIfMovedTo(EYE_POS.RIGHT)) {
                movedRight();
                return EYE_MOVE.RIGHT;
            }
        } else // lastPos == EYE_POS.RIGHT    // (6) przyjdzie otwarte, było prawo
        {
            if (checkIfMovedTo(EYE_POS.LEFT)) {
                movedLeft();
                return EYE_MOVE.LEFT;
            }
            if (checkIfMovedTo(EYE_POS.CENTER)) {
                movedCenter();
                return EYE_MOVE.CENTER;
            }
        }

        return EYE_MOVE.NONE;
    }


    // ZROBIONE GIT
    public void tickEyeClosed() {
        // fileLogger.write("pos_closed");
        if (lastPos == EYE_POS.CLOSED) // (1) Przyjdzie zamknięte, było zamknięte
        {
            return;  // GIT
        }

        // (2) Przyjdzie zamknięte, było otwarte
        // GIT
        addToPrevFramesList(EYE_POS.CLOSED);
        if (checkIfMovedTo(EYE_POS.CLOSED)) {
            closeEyes();
        }
    }

    private EYE_POS calcEyePupilPos(Point leftEyePupil, Rect leftEyeRect, Point rightEyePupil, Rect rightEyeRect) {
        double leftEyePos = (leftEyePupil.x - leftEyeRect.x) / (double) leftEyeRect.width;
        double rightEyePos = (rightEyePupil.x - rightEyeRect.x) / (double) rightEyeRect.width;

        double avgPos = (leftEyePos + rightEyePos) / 2.0;

        return xAxisPosToEYE_POS(avgPos);
    }

    private void addToPrevFramesList(EYE_POS pos) {
        eyePupilPosPrevFrames.remove(0);
        eyePupilPosPrevFrames.add(pos);
    }


    private boolean checkIfMovedTo(EYE_POS expectedPos) {
        for (int i = 0; i < PREVIOUS_FRAMES_LIST_SIZE; i++) {
            if (expectedPos != eyePupilPosPrevFrames.get(i)) {
                return false;
            }
        }

        return true;
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

    private void movedLeft() {
        canMoveRight = true;
        canMoveLeft = false;
        lastPos = EYE_POS.LEFT;
    }

    private void movedCenter() {
        canMoveRight = true;
        canMoveLeft = true;
        lastPos = EYE_POS.CENTER;
    }

    private void movedRight() {
        canMoveRight = false;
        canMoveLeft = true;
        lastPos = EYE_POS.RIGHT;
    }

    private void closeEyes() {
        canMoveLeft = false;
        canMoveRight = false;
        areEyesClosed = true;
        lastPos = EYE_POS.CLOSED;
    }

    private void openEyes(EYE_POS openInPos) {
        if (openInPos == EYE_POS.LEFT) {
            canMoveRight = true;
        } else if (openInPos == EYE_POS.RIGHT) {
            canMoveLeft = true;
        } else {
            canMoveRight = true;
            canMoveLeft = true;
        }

        areEyesClosed = false;
        lastPos = openInPos;
    }

    private enum EYE_POS {LEFT, CENTER, RIGHT, NONE, CLOSED}

    public enum EYE_MOVE {LEFT, CENTER, RIGHT, NONE}
}
