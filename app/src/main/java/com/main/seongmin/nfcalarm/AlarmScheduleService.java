package com.main.seongmin.nfcalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by seongmin on 12/21/16.
 */
public class AlarmScheduleService {

    public static void setAlarm(Context context, int alarmId, int hour, int minute, String nfcId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        String nfcUid;
        String nfcName;

        if (nfcId == context.getString(R.string.empty_nfc_id)) {
            nfcUid = context.getString(R.string.empty_nfc_uid);
            nfcName = context.getString(R.string.empty_nfc_name);
        } else {
            NFC nfc = DbHelper.getInstance(context).getNFCWithId(nfcId);
            nfcUid = nfc.getUid();
            nfcName = nfc.getName();
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(context.getString(R.string.intent_nfc_uid), nfcUid);
        intent.putExtra(context.getString(R.string.intent_nfc_name), nfcName);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    public static void cancelAlarm(Context context, int alarmId) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager!= null) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            alarmManager.cancel(PendingIntent.getBroadcast(
                    context.getApplicationContext(), alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

}
