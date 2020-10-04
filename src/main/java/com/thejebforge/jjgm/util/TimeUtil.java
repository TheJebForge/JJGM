package com.thejebforge.jjgm.util;

public class TimeUtil {
    private static double lastDelta = getClock();

    public static double getClock(){
        return System.nanoTime() / (double)1000000000;
    }

    public static double timeSince(double time){
        return getClock() - time;
    }

    public static double deltaTime(){
        double delta = timeSince(lastDelta);
        lastDelta = getClock();
        return delta;
    }
}
