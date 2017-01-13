package com.main.seongmin.nfcalarm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by seongmin on 12/21/16.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String nfcUid = intent.getStringExtra(context.getString(R.string.intent_nfc_uid));
        String nfcName = intent.getStringExtra(context.getString(R.string.intent_nfc_name));

        Intent service = new Intent(context, AlarmScheduleService.class);
        service.putExtra(context.getString(R.string.intent_nfc_uid), nfcUid);
        service.putExtra(context.getString(R.string.intent_nfc_name), nfcName);

        startWakefulService(context, service);
    }

}
