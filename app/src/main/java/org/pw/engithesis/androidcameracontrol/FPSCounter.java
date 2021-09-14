package org.pw.engithesis.androidcameracontrol;

import org.pw.engithesis.androidcameracontrol.interfaces.Observable;

public class FPSCounter extends Observable {
    private Long firstTimestamp;
    private Long previousTimestamp;
    private double frameCounter = 0.0;

    private double curFPS;
    private double avgFPS;

    public FPSCounter() {
        firstTimestamp = Long.valueOf(-1);
        previousTimestamp = System.currentTimeMillis();
    }

    public void tick() {
        frameCounter++;
        Long currentTime = System.currentTimeMillis();

        if (firstTimestamp == -1) {
            start();
        }

        curFPS = 1.0 / (currentTime - previousTimestamp) * 1000.0;
        avgFPS = frameCounter / (currentTime - firstTimestamp) * 1000.0;
        previousTimestamp = currentTime;

        notifyUpdate();
    }

    public double getCurFPS() {
        return curFPS;
    }

    public double getAvgFPS() {
        return avgFPS;
    }

    public void start() {
        firstTimestamp = System.currentTimeMillis();
    }
}
