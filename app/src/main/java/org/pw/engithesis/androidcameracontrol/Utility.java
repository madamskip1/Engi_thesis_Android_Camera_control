package org.pw.engithesis.androidcameracontrol;

public class Utility {
    private Utility() {
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}
