package com.main.seongmin.nfcalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.main.seongmin.nfcalarm.AlarmContract.AlarmEntry;

/**
 * Created by seongmin on 12/12/16.
 */

public class AlarmDbHelper extends SQLiteOpenHelper {

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " +
                    AlarmEntry.TABLE_NAME + " (" + AlarmEntry._ID + " INTEGER PRIMARY KEY," +
                    AlarmEntry.COLUMN_NAME_HOUR   + TYPE_INTEGER + COMMA_SEP +
                    AlarmEntry.COLUMN_NAME_MINUTE + TYPE_INTEGER + COMMA_SEP +
                    AlarmEntry.COLUMN_NAME_PERIOD + TYPE_INTEGER + COMMA_SEP +
                    AlarmEntry.COLUMN_NAME_NFC + TYPE_TEXT + " )";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + AlarmEntry.TABLE_NAME;


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NFCAlarm.db";

    public AlarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long saveAlarm(int hour, int minute, int period, String nfcId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values =  new ContentValues();
        values.put(AlarmEntry.COLUMN_NAME_HOUR, hour);
        values.put(AlarmEntry.COLUMN_NAME_MINUTE, minute);
        values.put(AlarmEntry.COLUMN_NAME_PERIOD, period);
        values.put(AlarmEntry.COLUMN_NAME_NFC, nfcId);

        long newAlarmId = db.insert(AlarmEntry.TABLE_NAME, null, values);

        return newAlarmId;
    }

    public Cursor loadAlarms() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                AlarmEntry._ID,
                AlarmEntry.COLUMN_NAME_HOUR,
                AlarmEntry.COLUMN_NAME_MINUTE,
                AlarmEntry.COLUMN_NAME_PERIOD,
                AlarmEntry.COLUMN_NAME_NFC
        };

        String sortOrder =  AlarmEntry.COLUMN_NAME_PERIOD + " ASC, " +
                            AlarmEntry.COLUMN_NAME_HOUR + " ASC, " +
                            AlarmEntry.COLUMN_NAME_MINUTE + " ASC";

        Cursor cursor = db.query(
                AlarmEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        return cursor;
    }

    public void deleteAlarm(String alarmId) {
        SQLiteDatabase db = getReadableDatabase();

        String selection = AlarmEntry._ID + " LIKE ?";
        String[] selectionArgs = { alarmId };
        db.delete(AlarmEntry.TABLE_NAME, selection, selectionArgs);
    }

    public int updateAlarm(String alarmId, int hour, int minute, int period, String nfcId) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(AlarmEntry.COLUMN_NAME_HOUR, hour);
        values.put(AlarmEntry.COLUMN_NAME_MINUTE, minute);
        values.put(AlarmEntry.COLUMN_NAME_PERIOD, period);
        values.put(AlarmEntry.COLUMN_NAME_NFC, nfcId);

        String selection = AlarmEntry._ID + " LIKE ?";
        String[] selectionArgs = { alarmId };

        int count = db.update(
                AlarmEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        return count;
    }

}
