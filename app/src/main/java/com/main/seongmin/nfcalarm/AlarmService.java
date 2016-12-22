package com.main.seongmin.nfcalarm;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by seongmin on 12/21/16.
 */
public class AlarmService extends IntentService {

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent alarmIntent = new Intent(this, AlarmActiveActivity.class);
        startActivity(alarmIntent);
        AlarmReceiver.completeWakefulIntent(intent);
    }



}
