package com.main.seongmin.nfcalarm;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import java.util.Calendar;

/**
 * Created by seongmin on 12/21/16.
 */
public class AlarmScheduleService extends IntentService {

    public AlarmScheduleService() { super("AlarmScheduleService"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        int alarmId = bundle.getInt(getString(R.string.intent_alarm_id), 0);
        String nfcName = bundle.getString(getString(R.string.intent_nfc_name));
        String nfcUid = bundle.getString(getString(R.string.intent_nfc_uid));

        Intent alarmActiveServiceIntent = new Intent(getApplicationContext(), AlarmActiveService.class);
        alarmActiveServiceIntent.putExtra(getString(R.string.intent_alarm_id), alarmId);
        alarmActiveServiceIntent.putExtra(getString(R.string.intent_nfc_name), nfcName);
        alarmActiveServiceIntent.putExtra(getString(R.string.intent_nfc_uid), nfcUid);

        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, alarmActiveServiceIntent,0);
        Notification alarmActiveNotification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_alarm_white_24dp)
                .setContentTitle(getString(R.string.alarm_active_notification_content_name))
                .setContentText(getString(R.string.alarm_active_notification_content_text))
                .setContentIntent(pendingIntent).build();

        startForeground(2441, alarmActiveNotification);
        startService(alarmActiveServiceIntent);
        AlarmReceiver.completeWakefulIntent(intent);
    }

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
