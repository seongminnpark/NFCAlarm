package com.main.seongmin.nfcalarm;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    private SimpleCursorAdapter nfcSpinnerCursorAdapter;

    public AlarmCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        String[] fromColumns = { NFCContract.NFCEntry.COLUMN_NAME_NAME };
        int[] toViews = { android.R.id.text1};
        Cursor nfcSpinnerCursor = DbHelper.getInstance(context).loadNFCsForSelection();
        nfcSpinnerCursorAdapter = new SimpleCursorAdapter(
                context, android.R.layout.simple_spinner_item, nfcSpinnerCursor, fromColumns, toViews,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        nfcSpinnerCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final DbHelper dbHelper = DbHelper.getInstance(context);

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
        String timeText = String.format("%d : %02d %s", hour12, minute, periodText);
        alarmItemTime.setText(timeText);

        // Switch setup.
        alarmSwitch.setChecked(enabled == 1);
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dbHelper.updateAlarm(alarmId, hour, minute, period, nfcId, 1);
                    AlarmScheduleService.setAlarm(context, Integer.parseInt(alarmId), hour, minute, nfcId);
                } else {
                    dbHelper.updateAlarm(alarmId, hour, minute, period, nfcId, 0);
                    AlarmScheduleService.cancelAlarm(context, Integer.parseInt(alarmId));
                }
            }
        });

        // Delete button setup.
        alarmItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteAlarm(alarmId);
                AlarmScheduleService.cancelAlarm(v.getContext(), Integer.parseInt(alarmId));
                MainActivity.alarmCursorAdapter.refreshAlarmList(dbHelper.loadAlarms());
            }
        });

        // NFC List Spinner setup.
        nfcSpinner.setAdapter(nfcSpinnerCursorAdapter);

        nfcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                Cursor nfcCursor = dbHelper.loadNFCsForSelection();
                nfcCursor.moveToPosition(position);
                if (!nfcCursor.isFirst()){
                    String newNFCId = nfcCursor.getString(nfcCursor.getColumnIndexOrThrow(
                            NFCContract.NFCEntry._ID));
                    dbHelper.updateAlarm(alarmId, hour, minute, period, newNFCId, enabled);
                    AlarmScheduleService.setAlarm(context, Integer.parseInt(alarmId), hour, minute, newNFCId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) { }
        });

    }

    public void refreshAlarmList(Cursor cursor) {
        getCursor().close();
        swapCursor(cursor);
    }

    public void refreshNFCListInSpinner(Cursor cursor) {
        nfcSpinnerCursorAdapter.swapCursor(cursor);
    }

}


