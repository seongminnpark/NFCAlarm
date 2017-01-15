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
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by seongmin on 12/21/16.
 */
public class AlarmActiveActivity extends AppCompatActivity {

    private int alarmId;
    private String nfcId;
    private String nfcUid;
    private String nfcName;
    private boolean noNFC;

    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private TextView alarmTapInstructionTextView;
    private Button alarmDismissButton;

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
                dismissAlarm();
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
        noNFC = getIntent().getBooleanExtra(getString(R.string.intent_no_nfc), true);
        nfcId = getIntent().getStringExtra(getString(R.string.intent_nfc_id));
        nfcUid = getIntent().getStringExtra(getString(R.string.intent_nfc_uid));
        nfcName = getIntent().getStringExtra(getString(R.string.intent_nfc_name));

        // Hide navigation bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        // Start alarm active service.
        startAlarmActiveService(alarmId, noNFC, nfcId, nfcUid, nfcName);

        alarmTapInstructionTextView = (TextView) findViewById(R.id.alarmActiveTapInstruction);
        String tapInstruction;
        if (noNFC) {
            tapInstruction = getString(R.string.alarm_active_tap_instruction_no_nfc);
        } else {
            tapInstruction = getString(R.string.alarm_active_tap_instruction) + " " + nfcName;
        }
        alarmTapInstructionTextView.setText(tapInstruction);

        // If no nfc was assigned, enable dismiss button.
        alarmDismissButton = (Button) findViewById(R.id.alarmDismissButton);
        if (noNFC) {
            alarmDismissButton.setEnabled(true);
            alarmDismissButton.setVisibility(View.VISIBLE);
            alarmDismissButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissAlarm();
                }
            });

            // No need to listen for nfc.
            return;

        } else {
            alarmDismissButton.setEnabled(false);
            alarmDismissButton.setVisibility(View.INVISIBLE);
        }

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

        if (!noNFC) {
            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
            nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
        }
    }

    private void startAlarmActiveService(int alarmId, boolean noNFC, String nfcId, String nfcUid, String nfcName) {

        Intent alarmActiveServiceIntent = new Intent(getApplicationContext(), AlarmActiveService.class);
        alarmActiveServiceIntent.putExtra(getApplicationContext().getString(R.string.intent_alarm_id), alarmId);
        alarmActiveServiceIntent.putExtra(getApplicationContext().getString(R.string.intent_no_nfc), noNFC);
        alarmActiveServiceIntent.putExtra(getApplicationContext().getString(R.string.intent_nfc_id), nfcId);
        alarmActiveServiceIntent.putExtra(getApplicationContext().getString(R.string.intent_nfc_uid), nfcUid);
        alarmActiveServiceIntent.putExtra(getApplicationContext().getString(R.string.intent_nfc_name), nfcName);

        startService(alarmActiveServiceIntent);
    }

    private void dismissAlarm() {
        Intent stopAlarmIntent = new Intent(getString(R.string.stop_alarm_intent));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(stopAlarmIntent);
        if (MainActivity.active) {
            finish();
        } else {
            finishAndRemoveTask();
        }
    }

}
