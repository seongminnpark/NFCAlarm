package com.main.seongmin.nfcalarm;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by seongmin on 12/22/16.
 */
public class AlarmListFragment extends Fragment {

    private ListView alarmListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        alarmListView = (ListView) rootView.findViewById(R.id.alarmListView);
        alarmListView.setAdapter(MainActivity.alarmCursorAdapter);
        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Cursor alarmCursor = MainActivity.alarmCursorAdapter.getCursor();
                alarmCursor.moveToPosition(pos);
                String alarmId = alarmCursor.getString(alarmCursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry._ID));
                MainActivity.dbHelper.deleteAlarm(alarmId);
                MainActivity.alarmReceiver.cancelAlarm(getActivity(), Integer.parseInt(alarmId));
                MainActivity.alarmCursorAdapter.refreshAlarmList(MainActivity.dbHelper.loadAlarms());
            }
        });

        return rootView;
    }
}
