package com.main.seongmin.nfcalarm;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;

import com.main.seongmin.nfcalarm.AlarmContract.AlarmEntry;

public class MainActivity extends AppCompatActivity {
    private static AlarmDbHelper alarmDbHelper;
    private static AlarmCursorAdapter alarmAdapter;

    private ListView alarmListView;
    private FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmDbHelper = new AlarmDbHelper(getApplicationContext());
        alarmListView = (ListView) findViewById(R.id.listView);
        addButton = (FloatingActionButton) findViewById(R.id.addButton);

        // Set up listView.
        alarmAdapter = new AlarmCursorAdapter(this, alarmDbHelper.loadAlarms());
        alarmListView.setAdapter(alarmAdapter);
        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Cursor alarmCursor = alarmAdapter.getCursor();
                alarmCursor.moveToPosition(pos);
                String alarmId = alarmCursor.getString(alarmCursor.getColumnIndexOrThrow(AlarmEntry._ID));
                alarmDbHelper.deleteAlarm(alarmId);
                alarmAdapter.refreshAlarmList(alarmDbHelper.loadAlarms());
            }
        });

        // Configure button.
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String time = Integer.toString(hourOfDay) + ":" + Integer.toString(minute);
            alarmDbHelper.saveAlarm(time, "dkjncksj");
            alarmAdapter.refreshAlarmList(alarmDbHelper.loadAlarms());
        }
    }
}
