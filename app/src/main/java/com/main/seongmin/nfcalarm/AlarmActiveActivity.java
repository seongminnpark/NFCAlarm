package com.main.seongmin.nfcalarm;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by seongmin on 12/21/16.
 */
public class AlarmActiveActivity extends AppCompatActivity {

    private int alarmId;
    private String nfcUid;
    private String nfcName;

    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private TextView alarmTapInstructionTextView;

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
                Intent stopAlarmIntent = new Intent(getString(R.string.stop_alarm_intent));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(stopAlarmIntent);
                if (MainActivity.active) {
                    finish();
                } else {
                    finishAndRemoveTask();
                }
            } else {
                alarmTapInstructionTextView.setText(Utils.convertTagIDToHexString(tag.getId()));
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_activate);

        alarmId = getIntent().getIntExtra(getString(R.string.intent_alarm_id), 0);
        nfcUid = getIntent().getStringExtra(getString(R.string.intent_nfc_uid));
        nfcName = getIntent().getStringExtra(getString(R.string.intent_nfc_name));

        // Start alarm active service.
        startAlarmActiveService(alarmId, nfcUid, nfcName);

        // Hide navigation bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        alarmTapInstructionTextView = (TextView) findViewById(R.id.alarmActiveTapInstruction);
        String tapInstruction = getString(R.string.alarm_active_tap_instruction) + " " + nfcName;
        alarmTapInstructionTextView.setText(tapInstruction);

        // Listen for incoming nfc.
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null || nfcAdapter.isEnabled()) {
            nfcPendingIntent = PendingIntent.getActivity(
                    this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    private void startAlarmActiveService(int alarmId, String nfcUid, String nfcName) {

        Intent alarmActiveServiceIntent = new Intent(getApplicationContext(), AlarmActiveService.class);
        alarmActiveServiceIntent.putExtra(getApplicationContext().getString(R.string.intent_alarm_id), alarmId);
        alarmActiveServiceIntent.putExtra(getApplicationContext().getString(R.string.intent_nfc_uid), nfcUid);
        alarmActiveServiceIntent.putExtra(getApplicationContext().getString(R.string.intent_nfc_name), nfcName);

        startService(alarmActiveServiceIntent);
    }

}
