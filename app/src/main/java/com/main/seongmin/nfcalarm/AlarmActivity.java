package com.main.seongmin.nfcalarm;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by seongmin on 12/21/16.
 */
public class AlarmActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private TextView alarmTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        alarmTextView = (TextView) findViewById(R.id.alarmText);

        if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
            return;
        }


    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        String action = intent.getAction();
        alarmTextView = (TextView) findViewById(R.id.alarmText);

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag == null) {
                alarmTextView.setText("tag null");
            } else {
                alarmTextView.setText(Arrays.toString(tag.getId()));
            }
            //finish();
        }
    }

}
