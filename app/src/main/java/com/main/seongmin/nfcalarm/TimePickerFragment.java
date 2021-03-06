package com.main.seongmin.nfcalarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

/**
 * Created by seongmin on 12/25/16.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

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

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);

        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        DbHelper dbHelper = DbHelper.getInstance(getContext().getApplicationContext());
        Cursor nfcCursor = dbHelper.loadNFCsForSelection();
        String firstNFCID;
        nfcCursor.moveToFirst();
        firstNFCID = nfcCursor.getString(nfcCursor.getColumnIndexOrThrow(NFCContract.NFCEntry._ID));
        int period = hour < 12 ? 0 : 1;
        int alarmId = dbHelper.saveAlarm(hour, minute, period, firstNFCID, 1);
        if (alarmId != -1) {
            AlarmScheduleService.setAlarm(getContext(), alarmId, hour, minute, firstNFCID);
        }
        MainActivity.alarmCursorAdapter.refreshAlarmList(dbHelper.loadAlarms());
        MainActivity.showTab(MainActivity.TAB_ALARM);
    }
}
