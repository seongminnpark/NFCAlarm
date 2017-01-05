package com.main.seongmin.nfcalarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;

/**
 * Created by seongmin on 12/21/16.
 */
public class AlarmActiveActivity extends AppCompatActivity {

    private final static int ALARM_ACTIVE_NOTIFICATION_CODE = 244;

    private MediaPlayer alarmPlayer;

    private NfcAdapter nfcAdapter;
    private TextView alarmTapInstructionTextView;

    private PendingIntent nfcPendingIntent;

    private String nfcUid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_activate);

        // Hide navigation bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        alarmTapInstructionTextView = (TextView) findViewById(R.id.alarmActiveTapInstruction);
        nfcUid = getIntent().getStringExtra(getString(R.string.intent_nfc_uid));
        String nfcName = getIntent().getStringExtra(getString(R.string.intent_nfc_name));
        String tapInstruction = getString(R.string.alarm_active_tap_instruction) + " " + nfcName;
        alarmTapInstructionTextView.setText(tapInstruction);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
            return;
        }

        nfcPendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        AudioManager audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
        if (alarmPlayer == null) {
            alarmPlayer = MediaPlayer.create(getApplicationContext(),
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        }

        try {
            float volume = (float) (audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
            alarmPlayer.setVolume(volume, volume);
        } catch (Exception e) {
            e.printStackTrace();
        }

        showAlarmActiveNotification();
        alarmPlayer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final String action = intent.getAction();

        if (action == null) {

        } else if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String tagId = Utils.convertTagIDToHexString(tag.getId());

            if (tag == null) {
                alarmTapInstructionTextView.setText(getString(R.string.invalid_tag_tapped));
            } else if (tagId.equals(nfcUid)) {
                NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notifyManager.cancel(ALARM_ACTIVE_NOTIFICATION_CODE);
                stopAlarm();
                finish();
            } else {
                alarmTapInstructionTextView.setText(Utils.convertTagIDToHexString(tag.getId()));
            }
        }
    }

    public void stopAlarm() {
        alarmPlayer.stop();
    }

    private void showAlarmActiveNotification() {
        NotificationCompat.Builder builder =  (android.support.v7.app.NotificationCompat.Builder)
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_alarm_white_24dp)
                        .setContentTitle(getString(R.string.alarm_active_notification_content_title))
                        .setContentText(getString(R.string.alarm_active_notification_content_text));
        builder.setOngoing(true);

        Intent resultIntent = new Intent(this, this.getClass());

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyManager.notify(ALARM_ACTIVE_NOTIFICATION_CODE, builder.build());
    }

}
