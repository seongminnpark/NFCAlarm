package com.main.seongmin.nfcalarm;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
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
        Switch alarmSwitch = (Switch) view.findViewById(R.id.itemAlarmSwitch);

        int index = cursor.getPosition();

        // Extract alarm info.
        final String alarmId = cursor.getString(cursor.getColumnIndexOrThrow(AlarmEntry._ID));
        final int hour = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_NAME_HOUR));
        final int minute = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_NAME_MINUTE));
        final int period = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_NAME_PERIOD));
        final String nfcId = cursor.getString(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_NAME_NFC));

        // Time display setup.
        int hour12 = hour % 12;
        if (hour12 == 0) { hour12 = 12; }
        String periodText = period == 0? "AM" : "PM";
        String timeText = String.format("%d : %d %s", hour12, minute, periodText);
        alarmItemTime.setText(timeText);

        // Switch setup.
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
    }

    public void refreshAlarmList(Cursor cursor) {
        getCursor().close();
        swapCursor(cursor);
    }

}


