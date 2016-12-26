package com.main.seongmin.nfcalarm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by seongmin on 12/21/16.
 */
public class AlarmService extends IntentService {

    public AlarmService() { super("AlarmService"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        String nfcName = bundle.getString(getString(R.string.intent_nfc_name));
        String nfcUid = bundle.getString(getString(R.string.intent_nfc_uid));

        Intent alarmIntent = new Intent(this, AlarmActiveActivity.class);
        alarmIntent.putExtra(getString(R.string.intent_nfc_name), nfcName);
        alarmIntent.putExtra(getString(R.string.intent_nfc_uid), nfcUid);

        startActivity(alarmIntent);
        AlarmReceiver.completeWakefulIntent(intent);
    }





}
