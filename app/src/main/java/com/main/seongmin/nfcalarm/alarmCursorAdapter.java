package com.main.seongmin.nfcalarm;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.main.seongmin.nfcalarm.AlarmContract.AlarmEntry;

/**
 * Created by seongmin on 12/21/16.
 */
public class AlarmCursorAdapter extends CursorAdapter {

    public AlarmCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView alarmItemTime = (TextView) view.findViewById(R.id.itemAlarmTime);
        ImageButton alarmItemDelete = (ImageButton) view.findViewById(R.id.itemAlarmDelete);
        Switch alarmSwitch = (Switch) view.findViewById(R.id.itemAlarmSwitch);
        Spinner nfcSpinner = (Spinner) view.findViewById(R.id.itemAlarmNFCSpinner);

        // Extract alarm info.
        final String alarmId = cursor.getString(cursor.getColumnIndexOrThrow(AlarmEntry._ID));
        final int hour = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_NAME_HOUR));
        final int minute = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_NAME_MINUTE));
        final int period = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_NAME_PERIOD));
        final String nfcId = cursor.getString(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_NAME_NFC));
        final int enabled = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_NAME_ENABLED));

        // Time display setup.
        int hour12 = hour % 12;
        if (hour12 == 0) { hour12 = 12; }
        String periodText = period == 0? "AM" : "PM";
        String timeText = String.format("%d : %d %s", hour12, minute, periodText);
        alarmItemTime.setText(timeText);

        // Switch setup.
        alarmSwitch.setChecked(enabled == 1 ? true : false);
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.dbHelper.updateAlarm(alarmId, hour, minute, period, nfcId, 1);
                    MainActivity.alarmReceiver.setAlarm(context, Integer.parseInt(alarmId), hour, minute);
                } else {
                    MainActivity.dbHelper.updateAlarm(alarmId, hour, minute, period, nfcId, 0);
                    MainActivity.alarmReceiver.cancelAlarm(context, Integer.parseInt(alarmId));
                }
            }
        });

        // Delete button setup.
        alarmItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.dbHelper.deleteAlarm(alarmId);
                MainActivity.alarmReceiver.cancelAlarm(v.getContext(), Integer.parseInt(alarmId));
                MainActivity.alarmCursorAdapter.refreshAlarmList(MainActivity.dbHelper.loadAlarms());
            }
        });

        // NFC List Spinner setup.
        String[] fromColumns = { NFCContract.NFCEntry.COLUMN_NAME_NAME };
        int[] toViews = { android.R.id.text1};
        Cursor nfcSpinnerCursor = MainActivity.dbHelper.loadNFCs();
        SimpleCursorAdapter nfcSpinnerCursorAdapter = new SimpleCursorAdapter(
                context, android.R.layout.simple_spinner_item, nfcSpinnerCursor, fromColumns, toViews,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        nfcSpinnerCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nfcSpinner.setAdapter(nfcSpinnerCursorAdapter);


    }

    public void refreshAlarmList(Cursor cursor) {
        getCursor().close();
        swapCursor(cursor);
    }

}


