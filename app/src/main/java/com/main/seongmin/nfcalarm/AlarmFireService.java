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

        Intent service = new Intent(getApplicationContext(), AlarmActiveService.class);
        service.putExtra(getApplicationContext().getString(R.string.intent_alarm_id), alarmId);
        service.putExtra(getApplicationContext().getString(R.string.intent_nfc_uid), nfcUid);
        service.putExtra(getApplicationContext().getString(R.string.intent_nfc_name), nfcName);

        startService(service);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, service, 0);

        Notification alarmActiveNotification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_alarm_white_24dp)
                .setContentTitle(getApplicationContext().getString(R.string.alarm_active_notification_content_name))
                .setContentText(getApplicationContext().getString(R.string.alarm_active_notification_content_text))
                .setContentIntent(pendingIntent).build();

        //startForeground(2441, alarmActiveNotification);

        AlarmReceiver.completeWakefulIntent(intent);
    }

}
