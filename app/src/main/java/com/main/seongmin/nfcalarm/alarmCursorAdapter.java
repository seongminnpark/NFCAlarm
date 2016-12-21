package com.main.seongmin.nfcalarm;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
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
    public void bindView(View view, Context context, Cursor cursor) {
        TextView alarmItem = (TextView) view.findViewById(R.id.item_alarm);
        String timeQuery = cursor.getString(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_NAME_TIME));

        String hour = Alarm.getHour(timeQuery);
        String minute = Alarm.getMinute(timeQuery);
        String am = Alarm.getAM(timeQuery) ? "AM" : "PM";
        String timeText = String.format("%s : %s %s", hour, minute, am);
        alarmItem.setText(timeText);
    }

    public void refreshAlarmList(Cursor cursor) {
        getCursor().close();
        swapCursor(cursor);
    }

}


