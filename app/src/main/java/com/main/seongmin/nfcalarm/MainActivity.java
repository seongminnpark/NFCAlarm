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

        // Test data source.
        ArrayList<String> mockData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mockData.add("11:30 am");
        }

        ArrayAdapter<String> mockAdapter = new ArrayAdapter<>(this, R.layout.alarm_list_item, mockData);
        listView.setAdapter(mockAdapter);
    }


    private long saveAlarm(String time, String nfcId) {
        SQLiteDatabase db = alarmDbHelper.getWritableDatabase();

        ContentValues values =  new ContentValues();
        values.put(AlarmEntry.COLUMN_NAME_TITLE, time);
        values.put(AlarmEntry.COLUMN_NAME_SUBTITLE, nfcId);

        long newRowId = db.insert(AlarmEntry.TABLE_NAME, null, values);

        return newRowId;
    }

    private long loadAlarm() {
        SQLiteDatabase db = alarmDbHelper.getReadableDatabase();

        String[] projection = {
                AlarmEntry._ID,
                AlarmEntry.COLUMN_NAME_TITLE,
                AlarmEntry.COLUMN_NAME_SUBTITLE
        };

        String selection = AlarmEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { "My Title" };

        String sortOrder =  AlarmEntry.COLUMN_NAME_SUBTITLE + " DESC";

        Cursor cursor = db.query(
            AlarmEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
            );

        cursor.moveToFirst();
        long itemId = cursor.getLong(
                cursor.getColumnIndexOrThrow(AlarmEntry._ID)
        );

        return itemId;
    }

    private void deleteAlarm(String alarmId) {
        SQLiteDatabase db = alarmDbHelper.getReadableDatabase();

        String selection = AlarmEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = { alarmId };
        db.delete(AlarmEntry.TABLE_NAME, selection, selectionArgs);
    }

    private int updateAlarm(String alarmId) {
        SQLiteDatabase db = alarmDbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(AlarmEntry.COLUMN_NAME_TITLE, alarmId);

        String selection = AlarmEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = { "My Title" };

        int count = db.update(
                AlarmEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        return count;
    }
}
