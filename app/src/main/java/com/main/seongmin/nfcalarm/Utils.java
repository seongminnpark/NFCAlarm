package com.main.seongmin.nfcalarm;

/**
 * Created by seongmin on 12/24/16.
 */
public class Utils {

    public String convertTagIDToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            int shaved = b & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(shaved));
        }
        return sb.toString();
    }
}
