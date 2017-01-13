package com.main.seongmin.nfcalarm;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by seongmin on 1/5/17.
 */
public class AlarmActiveService extends Service {

    public static final int NFC_NULL = 0;
    public static final int NFC_VALID = 1;
    public static final int NFC_INVALID = 2;

    private int alarmId;
    private String nfcUid;
    private String nfcName;
    private MediaPlayer alarmPlayer;

    private final BroadcastReceiver nfcReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            System.out.println("Tag discovered!!");
            final String action = intent.getAction();

            if (action == null) {

            } else if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String tagId = Utils.convertTagIDToHexString(tag.getId());

                if (tag == null) {
                    sendCommand(AlarmActiveService.NFC_NULL);
                } else if (tagId.equals(nfcUid)) {
                    sendCommand(AlarmActiveService.NFC_VALID);
                } else {
                    sendCommand(AlarmActiveService.NFC_INVALID);
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        alarmId = intent.getIntExtra(getString(R.string.intent_alarm_id),0);
        nfcUid = intent.getStringExtra(getString(R.string.intent_nfc_uid));
        nfcName = intent.getStringExtra(getString(R.string.intent_nfc_name));

        // Show alarm active activity.
        startAlarmActiveActivity();

        // Set up NFC Receiver.
        IntentFilter nfcFilter = new IntentFilter();
        nfcFilter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        registerReceiver(nfcReceiver, nfcFilter);

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
        unregisterReceiver(nfcReceiver);
    }

    private void startAlarmActiveActivity() {
        Intent alarmActiveActivityIntent = new Intent(getApplicationContext(), AlarmActiveActivity.class);
        alarmActiveActivityIntent.putExtra(getString(R.string.intent_nfc_uid), nfcUid);
        alarmActiveActivityIntent.putExtra(getString(R.string.intent_nfc_name), nfcName);

        startActivity(alarmActiveActivityIntent);
    }

    private void sendCommand(int nfcState) {

        // Dismiss alarm.
        Intent intent = new Intent(getString(R.string.alarm_active_intent_name));
        intent.putExtra(getString(R.string.intent_nfc_state), nfcState);
        intent.putExtra(getString(R.string.intent_nfc_uid), nfcUid);
        intent.putExtra(getString(R.string.intent_nfc_name), nfcName);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        if (nfcState == AlarmActiveService.NFC_VALID) {
            // Stop alarm sound.
            alarmPlayer.stop();
            AlarmScheduleService.cancelAlarm(getApplicationContext(), alarmId);
            stopSelf();
        }
    }

}
