package com.main.seongmin.nfcalarm;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by seongmin on 12/21/16.
 */
public class alarmCursorAdapter extends CursorAdapter {

    public alarmCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        
    }
}
