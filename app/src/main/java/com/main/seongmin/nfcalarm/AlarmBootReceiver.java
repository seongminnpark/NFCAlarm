package com.main.seongmin.nfcalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

/**
 * Created by seongmin on 12/24/16.
 */
public class AlarmBootReceiver extends BroadcastReceiver {
    AlarmReceiver alarm = new AlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Cursor alarmCursor = DbHelper.getInstance(context).loadAlarms();
            if (alarmCursor.moveToFirst()) {
                while (!alarmCursor.isAfterLast()) {
                    int enabled = alarmCursor.getInt(alarmCursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_NAME_ENABLED));
                    if (enabled == 1)  {
                        int alarmId = alarmCursor.getInt(alarmCursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry._ID));
                        int hour = alarmCursor.getInt(alarmCursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_NAME_HOUR));
                        int minute = alarmCursor.getInt(alarmCursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_NAME_MINUTE));
                        String nfcId = alarmCursor.getString(alarmCursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_NAME_NFC));
                        AlarmScheduleService.setAlarm(context.getApplicationContext(), alarmId, hour, minute, nfcId);
                    }
                    alarmCursor.moveToNext();
                }
            }
            alarmCursor.close();
        }
    }
}
