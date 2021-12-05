package org.pw.engithesis.androidcameracontrol;

import org.pw.engithesis.androidcameracontrol.interfaces.Observable;

import java.util.ArrayList;

public class EyeBlinkDetector extends Observable {

    private final EyesClosedQueue closedQueue = new EyesClosedQueue();
    private boolean blink = true;

    public boolean checkEyeBlink(boolean eyesClosed) {
        blink = closedQueue.isBlink(eyesClosed);
        if (blink) {
            notifyUpdate();
        }

        return blink;
    }

    public boolean isBlinking() {
        return blink;
    }

    private static class EyesClosedQueue {
        private static final int CONCUSSIVE_FRAME_TOLERANCE = 3;
        private final ArrayList<Boolean> previousFramesWasClosed;
        private boolean isClosed = false;

        EyesClosedQueue() {
            previousFramesWasClosed = new ArrayList<>();
            for (int i = 0; i < CONCUSSIVE_FRAME_TOLERANCE; i++) {
                previousFramesWasClosed.add(false);
            }
        }

        public boolean isBlink(boolean closed) {
            addToFramesList(closed);
            boolean stateChanged = didStateChange();
            if (!stateChanged) {
                return false;
            }

            isClosed = !isClosed;
            return isClosed;
        }

        private void addToFramesList(boolean closed) {
            previousFramesWasClosed.remove(0);
            previousFramesWasClosed.add(closed);
        }

        private boolean didStateChange() {
            for (int i = 0; i < CONCUSSIVE_FRAME_TOLERANCE; i++) {
                if (previousFramesWasClosed.get(i) == isClosed) {
                    return false;
                }
            }
            return true;
        }
    }
}
