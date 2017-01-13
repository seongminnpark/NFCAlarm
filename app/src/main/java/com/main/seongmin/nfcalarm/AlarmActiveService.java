package com.main.seongmin.nfcalarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by seongmin on 1/5/17.
 */
public class AlarmActiveService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
