package com.main.seongmin.nfcalarm;

/**
 * Created by seongmin on 12/21/16.
 */
public class Alarm {

    public static String getHour(String rawQuery) {
        String[] parsed = rawQuery.split(":");
        int format12 = Integer.parseInt(parsed[0]) % 12;
        return Integer.toString(format12);
    }

    public static String getMinute(String rawQuery) {
        String[] parsed = rawQuery.split(":");
        return parsed[1];
    }

    public static boolean getAM(String rawQuery) {
        String[] parsed = rawQuery.split(":");
        return Integer.parseInt(parsed[0]) < 12;
    }

}
