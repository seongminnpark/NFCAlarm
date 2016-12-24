package com.main.seongmin.nfcalarm;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.util.Calendar;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Created by seongmin on 12/22/16.
 */
public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabPagerAdapter pagerAdapter;

    public static DbHelper dbHelper;
    public static AlarmCursorAdapter alarmCursorAdapter;
    public static AlarmReceiver alarmReceiver;

    public static NFCCursorAdapter nfcCursorAdapter;

    public static NfcAdapter nfcAdapter;
    public static PendingIntent nfcPendingIntent;

    private NFCAddDialog nfcAddDialog;

    private FloatingActionButton addButton, addAlarmButton, addNFCButton;

    private Animation toX, toPlus, appear, disappear;
    private boolean fabOpen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Tabs setup.
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        // Alarms setup.
        dbHelper = new DbHelper(getApplicationContext());
        alarmCursorAdapter = new AlarmCursorAdapter(this, dbHelper.loadAlarms());
        alarmReceiver = new AlarmReceiver();

        // Configure button.
        fabOpen = false;
        addButton = (FloatingActionButton) findViewById(R.id.addButton);
        addAlarmButton = (FloatingActionButton) findViewById(R.id.addAlarmButton);
        addNFCButton = (FloatingActionButton) findViewById(R.id.addNFCButton);
        addAlarmButton.setClickable(false);
        addNFCButton.setClickable(false);
        setOnclickListeners();

        // Set up nfc.
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcCursorAdapter = new NFCCursorAdapter(this, dbHelper.loadNFCs());

        if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
            return;
        }

        nfcPendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Animation setup.
        toX = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tox);
        toPlus = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.toplus);
        appear = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.appear);
        disappear = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.disappear);
    }

    public static void addNFC(String name, String uid) {
        int alarmId = dbHelper.saveNFC(name, uid);
        nfcCursorAdapter.refreshNFCList(dbHelper.loadNFCs());
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

        public void onTimeSet(TimePicker view, int hour, int minute) {
            int period = hour < 12 ? 0 : 1;
            int alarmId = dbHelper.saveAlarm(hour, minute, period, "dkjncksj");
            if (alarmId != -1) { alarmReceiver.setAlarm(getContext(), alarmId, hour, minute); }
            alarmCursorAdapter.refreshAlarmList(dbHelper.loadAlarms());
        }
    }

    private void animateFab() {
        if (fabOpen) {
            addButton.startAnimation(toPlus);
            addAlarmButton.startAnimation(disappear);
            addNFCButton.startAnimation(disappear);
            addAlarmButton.setClickable(false);
            addNFCButton.setClickable(false);
        } else {
            addButton.startAnimation(toX);
            addAlarmButton.startAnimation(appear);
            addNFCButton.startAnimation(appear);
            addAlarmButton.setClickable(true);
            addNFCButton.setClickable(true);
        }
        fabOpen = !fabOpen;
    }

    private void setOnclickListeners() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });

        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(), "Time Picker");
            }
        });

        addNFCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nfcAddDialog = new NFCAddDialog();
                nfcAddDialog.show(getSupportFragmentManager(), "Add NFC");

                IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
                nfcAdapter.enableForegroundDispatch(MainActivity.this, nfcPendingIntent, writeTagFilters, null);

                animateFab();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final String action = intent.getAction();

        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            TextView alarmTextView = nfcAddDialog.getTextView();
            if (tag == null) {
                alarmTextView.setText("tag null");
            } else {
                String hexTag = Utils.convertTagIDToHexString(tag.getId());
                alarmTextView.setText(hexTag);
            }
        }
    }


}
