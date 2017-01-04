package com.main.seongmin.nfcalarm;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by seongmin on 12/21/16.
 */
public class AlarmService extends IntentService {

    public AlarmService() { super("AlarmService"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        String nfcName = bundle.getString(getString(R.string.intent_nfc_name));
        String nfcUid = bundle.getString(getString(R.string.intent_nfc_uid));

        Intent alarmIntent = new Intent(getApplicationContext(), AlarmActiveActivity.class);
        alarmIntent.putExtra(getString(R.string.intent_nfc_name), nfcName);
        alarmIntent.putExtra(getString(R.string.intent_nfc_uid), nfcUid);

        startActivity(alarmIntent);
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
            NFC nfc = MainActivity.dbHelper.getNFCWithId(nfcId);
            nfcUid = nfc.getUid();
            nfcName = nfc.getName();
        }

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

    public static void cancelAlarm(Context context, int alarmId) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager!= null) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            alarmManager.cancel(PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

}
