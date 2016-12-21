package com.main.seongmin.nfcalarm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import com.main.seongmin.nfcalarm.AlarmContract.AlarmEntry;

public class MainActivity extends AppCompatActivity {
    private AlarmDbHelper alarmDbHelper;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmDbHelper = new AlarmDbHelper(getApplicationContext());
        listView = (ListView) findViewById(R.id.listView);

        saveAlarm("11:30 am", "nfc");
        saveAlarm("11:30 am", "nfc");
        saveAlarm("11:30 am", "nfc");

//        listView.setAdapter(mockAdapter);

        loadAlarms();
    }


    private void saveAlarm(String time, String nfcId) {
        SQLiteDatabase db = alarmDbHelper.getWritableDatabase();

        ContentValues values =  new ContentValues();
        values.put(AlarmEntry.COLUMN_NAME_TIME, time);
        values.put(AlarmEntry.COLUMN_NAME_NFC, nfcId);

        db.insert(AlarmEntry.TABLE_NAME, null, values);
    }

    private void loadAlarms() {
        SQLiteDatabase db = alarmDbHelper.getReadableDatabase();

        String[] projection = {
                AlarmEntry._ID,
                AlarmEntry.COLUMN_NAME_TIME,
                AlarmEntry.COLUMN_NAME_NFC
        };

        String selection = AlarmEntry.COLUMN_NAME_TIME + " = ?";
        String[] selectionArgs = { "11:30 am" };

        String sortOrder =  AlarmEntry.COLUMN_NAME_TIME + " DESC";

        Cursor cursor = db.query(
            AlarmEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
            );

        //cursor.moveToFirst();
    }

    private void deleteAlarm(String alarmId) {
        SQLiteDatabase db = alarmDbHelper.getReadableDatabase();

        String selection = AlarmEntry.COLUMN_NAME_TIME + " LIKE ?";
        String[] selectionArgs = { alarmId };
        db.delete(AlarmEntry.TABLE_NAME, selection, selectionArgs);
    }

    private int updateAlarm(String alarmId, String time, String nfcId) {
        SQLiteDatabase db = alarmDbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(AlarmEntry.COLUMN_NAME_TIME, time);
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
