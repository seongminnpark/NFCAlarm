package com.main.seongmin.nfcalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.main.seongmin.nfcalarm.AlarmContract.AlarmEntry;

/**
 * Created by seongmin on 12/12/16
 */

public class DbHelper extends SQLiteOpenHelper {

    private static DbHelper instance;

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ALARM_ENTRIES =
            "CREATE TABLE " +
                    AlarmEntry.TABLE_NAME + " (" + AlarmEntry._ID + " INTEGER PRIMARY KEY," +
                    AlarmEntry.COLUMN_NAME_HOUR   + TYPE_INTEGER + COMMA_SEP +
                    AlarmEntry.COLUMN_NAME_MINUTE + TYPE_INTEGER + COMMA_SEP +
                    AlarmEntry.COLUMN_NAME_PERIOD + TYPE_INTEGER + COMMA_SEP +
                    AlarmEntry.COLUMN_NAME_NFC + TYPE_TEXT + COMMA_SEP +
                    AlarmEntry.COLUMN_NAME_ENABLED + TYPE_INTEGER + " )";
    private static final String SQL_CREATE_NFC_ENTRIES =
            "CREATE TABLE " +
                    NFCContract.NFCEntry.TABLE_NAME + " (" + NFCContract.NFCEntry._ID + " INTEGER PRIMARY KEY," +
                    NFCContract.NFCEntry.COLUMN_NAME_NAME   + TYPE_TEXT + COMMA_SEP +
                    NFCContract.NFCEntry.COLUMN_NAME_UID + TYPE_TEXT + " )";

    private static final String SQL_DELETE_ALARM_ENTRIES = "DROP TABLE IF EXISTS " + AlarmEntry.TABLE_NAME;
    private static final String SQL_DELETE_NFC_ENTRIES = "DROP TABLE IF EXISTS " + NFCContract.NFCEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NFCAlarm.db";

    public static final String EMPTY_NFC_ID = "-1";                // Keep this in sync with R.string.empty_nfc_id.
    public static final String EMPTY_NFC_NAME = "No NFC Assigned"; // Keep this in sync with R.string.empty_nfc_name.
    public static final String EMPTY_NFC_UID = "NO NFC";           // Keep this in sync with R.string.empty_nfc_uid.

    // Call getInstance instead.
    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context.getApplicationContext());
        }
        return instance;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ALARM_ENTRIES);
        db.execSQL(SQL_CREATE_NFC_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ALARM_ENTRIES);
        db.execSQL(SQL_DELETE_NFC_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int saveAlarm(int hour, int minute, int period, String nfcId, int enabled) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values =  new ContentValues();
        values.put(AlarmEntry.COLUMN_NAME_HOUR, hour);
        values.put(AlarmEntry.COLUMN_NAME_MINUTE, minute);
        values.put(AlarmEntry.COLUMN_NAME_PERIOD, period);
        values.put(AlarmEntry.COLUMN_NAME_NFC, nfcId);
        values.put(AlarmEntry.COLUMN_NAME_ENABLED, enabled);

        long newAlarmId = db.insert(AlarmEntry.TABLE_NAME, null, values);

        return java.lang.Math.toIntExact(newAlarmId);
    }

    public Cursor loadAlarms() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                AlarmEntry._ID,
                AlarmEntry.COLUMN_NAME_HOUR,
                AlarmEntry.COLUMN_NAME_MINUTE,
                AlarmEntry.COLUMN_NAME_PERIOD,
                AlarmEntry.COLUMN_NAME_NFC,
                AlarmEntry.COLUMN_NAME_ENABLED
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

    public int updateAlarm(String alarmId, int hour, int minute, int period, String nfcId, int enabled) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(AlarmEntry.COLUMN_NAME_HOUR, hour);
        values.put(AlarmEntry.COLUMN_NAME_MINUTE, minute);
        values.put(AlarmEntry.COLUMN_NAME_PERIOD, period);
        values.put(AlarmEntry.COLUMN_NAME_NFC, nfcId);
        values.put(AlarmEntry.COLUMN_NAME_ENABLED, enabled);

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

    public int saveNFC(String name, String uid) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values =  new ContentValues();
        values.put(NFCContract.NFCEntry.COLUMN_NAME_NAME, name);
        values.put(NFCContract.NFCEntry.COLUMN_NAME_UID, uid);

        long newNFCId = db.insert(NFCContract.NFCEntry.TABLE_NAME, null, values);

        return java.lang.Math.toIntExact(newNFCId);
    }

    public Cursor loadNFCs() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                NFCContract.NFCEntry._ID,
                NFCContract.NFCEntry.COLUMN_NAME_NAME,
                NFCContract.NFCEntry.COLUMN_NAME_UID,
        };

        String sortOrder =  NFCContract.NFCEntry.COLUMN_NAME_NAME + " ASC, " +
                NFCContract.NFCEntry.COLUMN_NAME_UID + " ASC";

        Cursor cursor = db.query(
                NFCContract.NFCEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        return cursor;
    }

    public Cursor loadNFCsForSelection() {

        MatrixCursor defaultCursor = new MatrixCursor(new String[]{
                NFCContract.NFCEntry._ID,
                NFCContract.NFCEntry.COLUMN_NAME_NAME,
                NFCContract.NFCEntry.COLUMN_NAME_UID });

        // Add default row.
        defaultCursor.addRow(new String[] { EMPTY_NFC_ID, EMPTY_NFC_NAME, EMPTY_NFC_UID });

        // Dump nfc list into new cursorWithDefault.
        Cursor cursor = loadNFCs();

        Cursor[] cursors = { defaultCursor, cursor };
        Cursor cursorWithDefault = new MergeCursor(cursors);

//        if (cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            for (int i=0; i<cursor.getCount(); i++)
//            {
//                Object[] column = new Object[]{
//                        cursor.getInt(0),
//                        " [" + cursor.getString(1)+"]"+cursor.getString(2),
//                        cursor.getString(3)+" ["+cursor.getString(4)+"]",
//                        cursor.getString(5),
//                        cursor.getString(6)
//                };
//                result.addRow(column);
//                cursor.moveToNext();
//            }
//        }

        return cursorWithDefault;
    }

    public void deleteNFC(String nfcId) {
        SQLiteDatabase db = getReadableDatabase();

        String selection = NFCContract.NFCEntry._ID + " LIKE ?";
        String[] selectionArgs = { nfcId };
        db.delete(NFCContract.NFCEntry.TABLE_NAME, selection, selectionArgs);
    }

    public int updateNFC(String nfcId, String name, String uid) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(NFCContract.NFCEntry.COLUMN_NAME_NAME, name);
        values.put(NFCContract.NFCEntry.COLUMN_NAME_UID, uid);

        String selection = NFCContract.NFCEntry._ID + " LIKE ?";
        String[] selectionArgs = { nfcId };

        int count = db.update(
                NFCContract.NFCEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        return count;
    }

    public NFC getNFCWithId(String nfcId) {
        if (nfcId.equals(EMPTY_NFC_ID)) {
            return new NFC(EMPTY_NFC_NAME, EMPTY_NFC_NAME);
        }
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                NFCContract.NFCEntry._ID,
                NFCContract.NFCEntry.COLUMN_NAME_NAME,
                NFCContract.NFCEntry.COLUMN_NAME_UID,
        };

        String selection = NFCContract.NFCEntry._ID + " LIKE ?";
        String[] selectionArgs = { nfcId };

        Cursor cursor = db.query(
                NFCContract.NFCEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        NFC nfc;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String uid = cursor.getString(cursor.getColumnIndexOrThrow(NFCContract.NFCEntry.COLUMN_NAME_UID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(NFCContract.NFCEntry.COLUMN_NAME_NAME));
            nfc = new NFC(uid, name);
        } else {
            // This branch will never get to this point, because inputs are sanitized before function call.
            nfc = new NFC(EMPTY_NFC_NAME, EMPTY_NFC_NAME);
        }
        return nfc;
    }

}
