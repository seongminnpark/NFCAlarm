package com.main.seongmin.nfcalarm;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

/**
 * Created by seongmin on 1/14/17.
 */
public class AlarmFireService extends IntentService {

    public AlarmFireService () {
        super("AlarmFireIntent");
    }

    @Override
    public void onHandleIntent(Intent intent) {

        int alarmId = intent.getIntExtra(getString(R.string.intent_alarm_id),0);
        String nfcUid = intent.getStringExtra(getApplicationContext().getString(R.string.intent_nfc_uid));
        String nfcName = intent.getStringExtra(getApplicationContext().getString(R.string.intent_nfc_name));

        // Show alarm active activity.
        startAlarmActiveActivity(alarmId, nfcUid, nfcName);

        AlarmReceiver.completeWakefulIntent(intent);
    }

    private void startAlarmActiveActivity(int alarmId, String nfcUid, String nfcName) {
        Intent alarmActiveActivityIntent = new Intent(getApplicationContext(), AlarmActiveActivity.class);
        alarmActiveActivityIntent.putExtra(getApplicationContext().getString(R.string.intent_alarm_id), alarmId);
        alarmActiveActivityIntent.putExtra(getString(R.string.intent_nfc_uid), nfcUid);
        alarmActiveActivityIntent.putExtra(getString(R.string.intent_nfc_name), nfcName);

        startActivity(alarmActiveActivityIntent);
    }

}
