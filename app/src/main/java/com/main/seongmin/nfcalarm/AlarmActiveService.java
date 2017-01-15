package com.main.seongmin.nfcalarm;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;

/**
 * Created by seongmin on 1/5/17.
 */
public class AlarmActiveService extends Service {

    private int alarmId;
    private String nfcId;
    private String nfcUid;
    private String nfcName;
    private MediaPlayer alarmPlayer;

    private static final int ALARM_ACTIVE_NOTIFICATION_ID = 2441;

    private final BroadcastReceiver stopAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopAlarm();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        alarmId = intent.getIntExtra(getString(R.string.intent_alarm_id),0);
        nfcId = intent.getStringExtra(getString(R.string.intent_nfc_id));
        nfcUid = intent.getStringExtra(getString(R.string.intent_nfc_uid));
        nfcName = intent.getStringExtra(getString(R.string.intent_nfc_name));

        // Show notification.
        showNotification();

        // Set up alarm stop receiver.
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(stopAlarmReceiver,
                new IntentFilter(getString(R.string.stop_alarm_intent)));

        // Start playing alarm.
        AudioManager audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
        if (alarmPlayer == null) {
            alarmPlayer = MediaPlayer.create(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

            try {
                float volume = (float) (audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
                alarmPlayer.setVolume(volume, volume);
            } catch (Exception e) {
                e.printStackTrace();
            }

            alarmPlayer.start();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stopAlarmReceiver);
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, AlarmActiveActivity.class);
        notificationIntent.putExtra(getApplicationContext().getString(R.string.intent_alarm_id), alarmId);
        notificationIntent.putExtra(getString(R.string.intent_nfc_id), nfcId);
        notificationIntent.putExtra(getString(R.string.intent_nfc_uid), nfcUid);
        notificationIntent.putExtra(getString(R.string.intent_nfc_name), nfcName);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification alarmActiveNotification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_alarm_white_24dp)
                .setContentTitle(getApplicationContext().getString(R.string.alarm_active_notification_content_name))
                .setContentText(getApplicationContext().getString(R.string.alarm_active_notification_content_text))
                .setContentIntent(pendingIntent).build();

        startForeground(AlarmActiveService.ALARM_ACTIVE_NOTIFICATION_ID, alarmActiveNotification);
    }

    private void stopAlarm() {
        // Stop alarm sound.
        alarmPlayer.stop();
        //AlarmScheduleService.cancelAlarm(getApplicationContext(), alarmId);
        stopSelf();
    }

}
