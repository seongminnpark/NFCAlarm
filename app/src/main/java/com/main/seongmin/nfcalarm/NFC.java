package com.main.seongmin.nfcalarm;

/**
 * Created by seongmin on 12/26/16.
 */
public class NFC {

    private String uid;
    private String name;

    public NFC(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }
}
