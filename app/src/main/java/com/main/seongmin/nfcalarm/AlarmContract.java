package com.main.seongmin.nfcalarm;

import android.provider.BaseColumns;

/**
 * Created by seongmin on 12/12/16.
 */

public final class AlarmContract {

    // Prevent initialization of this class.
    private AlarmContract() {
    }

    public static class AlarmEntry implements BaseColumns {
        public static final String TABLE_NAME = "alarms";
        public static final String COLUMN_NAME_HOUR = "hour";
        public static final String COLUMN_NAME_MINUTE = "minute";
        public static final String COLUMN_NAME_PERIOD = "period";
        public static final String COLUMN_NAME_NFC = "nfc";
        public static final String COLUMN_NAME_ENABLED = "enabled";
    }

}
