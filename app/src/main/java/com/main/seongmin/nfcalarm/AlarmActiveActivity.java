package com.main.seongmin.nfcalarm;

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
import android.widget.TextView;

/**
 * Created by seongmin on 12/21/16.
 */
public class AlarmActiveActivity extends AppCompatActivity {

    private MediaPlayer alarmPlayer;

    private NfcAdapter nfcAdapter;
    private TextView alarmTapInstructionTextView;

    private PendingIntent nfcPendingIntent;

    private String nfcUid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_activate);

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
       alarmPlayer = MediaPlayer.create(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

        try {
            float volume = (float) (audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
            alarmPlayer.setVolume(volume, volume);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                finish();
            } else {
                alarmTapInstructionTextView.setText(Utils.convertTagIDToHexString(tag.getId()));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        alarmPlayer.stop();
    }

}
