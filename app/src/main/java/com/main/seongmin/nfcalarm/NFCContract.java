package com.main.seongmin.nfcalarm;

import android.provider.BaseColumns;

/**
 * Created by seongmin on 12/24/16.
 */
public class NFCContract {

    // Prevent initialization of this class.
    private NFCContract() {
    }

    public static class NFCEntry implements BaseColumns {
        public static final String TABLE_NAME = "nfcs";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_UID = "uid";
    }

}
