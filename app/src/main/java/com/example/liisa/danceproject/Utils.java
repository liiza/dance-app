package com.example.liisa.danceproject;

public class Utils {

    public static float nanoToSeconds(long diffInNanoSeconds) {
        return (float) (diffInNanoSeconds / Math.pow(10.0, 9.0));
    }

    public static long milliToNanoSeconds(long milliSeconds) {
        return (long) (milliSeconds * Math.pow(10, 6));
    }

    public static long nanoToMilliSeconds(long nanoSeconds) {
        return (long) (nanoSeconds / Math.pow(10, 6));
    }
}
