package com.main.seongmin.nfcalarm;

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

/**
 * Created by seongmin on 1/5/17.
 */
public class AlarmActiveService extends Service {

    private int alarmId;
    private String nfcUid;
    private String nfcName;
    private MediaPlayer alarmPlayer;

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
        nfcUid = intent.getStringExtra(getString(R.string.intent_nfc_uid));
        nfcName = intent.getStringExtra(getString(R.string.intent_nfc_name));

        // Set up alarm stop receiver.
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(stopAlarmReceiver,
                new IntentFilter(getString(R.string.stop_alarm_intent)));

        // Show alarm active activity.
        startAlarmActiveActivity();

        // Start playing alarm.
        AudioManager audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
        alarmPlayer = MediaPlayer.create(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

        try {
            float volume = (float) (audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
            alarmPlayer.setVolume(volume, volume);
        } catch (Exception e) {
            e.printStackTrace();
        }

        alarmPlayer.start();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(stopAlarmReceiver);
    }

    private void startAlarmActiveActivity() {
        Intent alarmActiveActivityIntent = new Intent(getApplicationContext(), AlarmActiveActivity.class);
        alarmActiveActivityIntent.putExtra(getString(R.string.intent_nfc_uid), nfcUid);
        alarmActiveActivityIntent.putExtra(getString(R.string.intent_nfc_name), nfcName);

        startActivity(alarmActiveActivityIntent);
    }

    private void stopAlarm() {
        // Stop alarm sound.
        alarmPlayer.stop();
        //AlarmScheduleService.cancelAlarm(getApplicationContext(), alarmId);
        stopSelf();
    }

}
