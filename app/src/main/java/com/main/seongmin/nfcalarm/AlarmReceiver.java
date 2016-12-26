package com.main.seongmin.nfcalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

/**
 * Created by seongmin on 12/21/16.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        String nfcUid = intent.getStringExtra(context.getString(R.string.intent_nfc_uid));
        String nfcName = intent.getStringExtra(context.getString(R.string.intent_nfc_name));

        Intent service = new Intent(context, AlarmService.class);
        service.putExtra(context.getString(R.string.intent_nfc_uid), nfcUid);
        service.putExtra(context.getString(R.string.intent_nfc_name), nfcName);

        startWakefulService(context, service);
    }

    public void setAlarm(Context context, int alarmId, int hour, int minute, String nfcId) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        NFC nfc = MainActivity.dbHelper.getNFCWithId(nfcId);
        String nfcUid = nfc.getUid();
        String nfcName = nfc.getName();

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(context.getString(R.string.intent_nfc_uid), nfcUid);
        intent.putExtra(context.getString(R.string.intent_nfc_name), nfcName);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    public void cancelAlarm(Context context, int alarmId) {
        if (alarmManager!= null) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            alarmManager.cancel(PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

}
