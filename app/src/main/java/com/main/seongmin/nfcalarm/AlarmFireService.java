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

        // Start alarm active service.
        startAlarmActiveService(alarmId, nfcUid, nfcName);

        // Show alarm active activity.
        startAlarmActiveActivity(alarmId, nfcUid, nfcName);

        AlarmReceiver.completeWakefulIntent(intent);
    }

    private void startAlarmActiveService(int alarmId, String nfcUid, String nfcName) {

        Intent alarmActiveServiceIntent = new Intent(getApplicationContext(), AlarmActiveService.class);
        alarmActiveServiceIntent.putExtra(getApplicationContext().getString(R.string.intent_alarm_id), alarmId);
        alarmActiveServiceIntent.putExtra(getApplicationContext().getString(R.string.intent_nfc_uid), nfcUid);
        alarmActiveServiceIntent.putExtra(getApplicationContext().getString(R.string.intent_nfc_name), nfcName);

        startService(alarmActiveServiceIntent);

    }

    private void startAlarmActiveActivity(int alarmId, String nfcUid, String nfcName) {
        Intent alarmActiveActivityIntent = new Intent(getApplicationContext(), AlarmActiveActivity.class);
        alarmActiveActivityIntent.putExtra(getApplicationContext().getString(R.string.intent_alarm_id), alarmId);
        alarmActiveActivityIntent.putExtra(getString(R.string.intent_nfc_uid), nfcUid);
        alarmActiveActivityIntent.putExtra(getString(R.string.intent_nfc_name), nfcName);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, alarmActiveActivityIntent, 0);

        Notification alarmActiveNotification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_alarm_white_24dp)
                .setContentTitle(getApplicationContext().getString(R.string.alarm_active_notification_content_name))
                .setContentText(getApplicationContext().getString(R.string.alarm_active_notification_content_text))
                .setContentIntent(pendingIntent).build();

        //startForeground(2441, alarmActiveNotification);

        startActivity(alarmActiveActivityIntent);
    }

}
