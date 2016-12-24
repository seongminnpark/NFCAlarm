package com.main.seongmin.nfcalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by seongmin on 12/24/16.
 */
public class NfcReceiver extends BroadcastReceiver {

    private TextView textView;

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Received!!!");
        final String action = intent.getAction();

        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag == null) {
                textView.setText("tag null");
            } else {
                textView.setText(convertTagIDToHexString(tag.getId()));
            }
        }
    }

    private String convertTagIDToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            int shaved = b & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(shaved));
        }
        return sb.toString();
    }

}
