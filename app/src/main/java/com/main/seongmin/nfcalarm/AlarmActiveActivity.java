package com.main.seongmin.nfcalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by seongmin on 12/21/16.
 */
public class AlarmActiveActivity extends AppCompatActivity {

    private TextView alarmTapInstructionTextView;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null ) {
                int nfcState = intent.getIntExtra(getString(R.string.intent_nfc_state), 0);
                String nfcUid = intent.getStringExtra(getString(R.string.intent_nfc_uid));
                String nfcName = intent.getStringExtra(getString(R.string.intent_nfc_name));
                String nfcText;
                switch(nfcState) {
                    case AlarmActiveService.NFC_NULL:
                        nfcText = getString(R.string.nfc_instruction_null);
                        break;
                    case AlarmActiveService.NFC_VALID:
                        nfcText = getString(R.string.nfc_instruction_valid);
                        break;
                    case AlarmActiveService.NFC_INVALID:
                        nfcText = getString(R.string.nfc_instruction_invalid);
                        break;
                    default:
                        nfcText = getString(R.string.nfc_instruction_default);
                        break;
                }
                alarmTapInstructionTextView.setText(nfcText);

                if (nfcState == AlarmActiveService.NFC_VALID) {
                    finish();
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_activate);

        // Hide navigation bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        alarmTapInstructionTextView = (TextView) findViewById(R.id.alarmActiveTapInstruction);
        String nfcName = getIntent().getStringExtra(getString(R.string.intent_nfc_name));
        String tapInstruction = getString(R.string.alarm_active_tap_instruction) + " " + nfcName;
        alarmTapInstructionTextView.setText(tapInstruction);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver,
                new IntentFilter(getString(R.string.alarm_active_intent_name)));
    }

}
