package com.main.seongmin.nfcalarm;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by seongmin on 12/21/16.
 */
public class AlarmActiveActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private TextView alarmTapInstructionTextView;

    private PendingIntent nfcPendingIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_activate);

        alarmTapInstructionTextView = (TextView) findViewById(R.id.alarmActiveTapInstruction);
        String nfcUid = getIntent().getStringExtra(getString(R.string.intent_nfc_uid));
        String nfcName = getIntent().getStringExtra(getString(R.string.intent_nfc_name));
        String tapInstruction = getString(R.string.alarm_active_tap_instruction) + " " + nfcName;
        alarmTapInstructionTextView.setText(tapInstruction);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
            return;
        }

        nfcPendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
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

        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag == null) {
                alarmTapInstructionTextView.setText("tag null");
            } else {
                alarmTapInstructionTextView.setText(Utils.convertTagIDToHexString(tag.getId()));
            }
        }
    }

}
