package com.main.seongmin.nfcalarm;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by seongmin on 1/14/17.
 */
public class AlarmFireService extends IntentService {

    public AlarmFireService () {
        super("AlarmFireIntent");
    }

    @Override
    public void onHandleIntent(Intent intent) {

        int alarmId = intent.getIntExtra(getString(R.string.intent_alarm_id),0);
        boolean noNFC = intent.getBooleanExtra(getString(R.string.intent_no_nfc), true);
        String nfcId = intent.getStringExtra(getApplicationContext().getString(R.string.intent_nfc_id));
        String nfcUid = intent.getStringExtra(getApplicationContext().getString(R.string.intent_nfc_uid));
        String nfcName = intent.getStringExtra(getApplicationContext().getString(R.string.intent_nfc_name));

        // Show alarm active activity.
        Intent alarmActiveActivityIntent = new Intent(getApplicationContext(), AlarmActiveActivity.class);
        alarmActiveActivityIntent.putExtra(getApplicationContext().getString(R.string.intent_alarm_id), alarmId);
        alarmActiveActivityIntent.putExtra(getString(R.string.intent_no_nfc), noNFC);
        alarmActiveActivityIntent.putExtra(getString(R.string.intent_nfc_id), nfcId);
        alarmActiveActivityIntent.putExtra(getString(R.string.intent_nfc_uid), nfcUid);
        alarmActiveActivityIntent.putExtra(getString(R.string.intent_nfc_name), nfcName);

        startActivity(alarmActiveActivityIntent);

        AlarmReceiver.completeWakefulIntent(intent);
    }
}
