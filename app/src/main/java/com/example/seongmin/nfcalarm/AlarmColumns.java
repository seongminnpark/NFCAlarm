package com.example.seongmin.nfcalarm;

import android.provider.BaseColumns;

/**
 * Created by seongmin on 12/12/16.
 */

public final class AlarmContract {

    // Prevent initialization of this class.
    private AlarmColumns() {}

    public static class AlarmEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLOMN_NAME = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
    }

}
