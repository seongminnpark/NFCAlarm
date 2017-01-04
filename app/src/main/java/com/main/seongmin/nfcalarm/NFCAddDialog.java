package com.main.seongmin.nfcalarm;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by seongmin on 12/24/16.
 */
public class NFCAddDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return getView();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.fragment_add_nfc, null))
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String name = getEditText().getText().toString().trim();
                                String uid = getTextView().getText().toString();
                                DbHelper dbHelper = DbHelper.getInstance(getContext().getApplicationContext());
                                dbHelper.saveNFC(name, uid);
                                MainActivity.nfcCursorAdapter.refreshNFCList(dbHelper.loadNFCs());
                            }
                        }
                )
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);

        final Activity activity = getActivity();
        MainActivity.nfcAdapter.disableForegroundDispatch(activity);

        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    public TextView getTextView() {
        return (TextView) getDialog().findViewById(R.id.addNFCTag);
    }

    public EditText getEditText() {
        return (EditText) getDialog().findViewById(R.id.addNFCName);
    }
}
