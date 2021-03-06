package com.main.seongmin.nfcalarm;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by seongmin on 12/22/16.
 */
public class MainActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    public static final int TAB_ALARM = 0;
    public static final int TAB_NFC = 1;

    public static boolean active = false;

    private TabLayout tabLayout;
    public static ViewPager viewPager;
    public static TabPagerAdapter pagerAdapter;

    private DbHelper dbHelper;

    public static AlarmCursorAdapter alarmCursorAdapter;
    public static AlarmReceiver alarmReceiver;

    public static NFCCursorAdapter nfcCursorAdapter;

    public static NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;

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
        dbHelper = DbHelper.getInstance(getApplicationContext());
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

        if (nfcAdapter == null) {
            Toast.makeText(getApplicationContext(), "This application requires NFC capability.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
            finish();
        } else if (!nfcAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Please activate NFC and press Back to return to the application!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }

        nfcPendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Animation setup.
        toX = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tox);
        toPlus = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.toplus);
        appear = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.appear);
        disappear = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.disappear);

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
                IntentFilter[] writeTagFilters = new IntentFilter[]{tagDetected};
                nfcAdapter.enableForegroundDispatch(MainActivity.this, nfcPendingIntent, writeTagFilters, null);
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

        // Enable add button only when nfc is scanned.
        ((AlertDialog)nfcAddDialog.getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        animateFab();
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    public static void showTab(int tabId) {
        MainActivity.viewPager.setCurrentItem(tabId, true);
    }

}
