package com.main.seongmin.nfcalarm;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by seongmin on 12/24/16.
 */
public class NFCCursorAdapter extends CursorAdapter {

    public NFCCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_nfc, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nfcItemName = (TextView) view.findViewById(R.id.itemNFCName);
        ImageButton nfcItemDelete = (ImageButton) view.findViewById(R.id.itemNFCDelete);

        final String nfcId = cursor.getString(cursor.getColumnIndexOrThrow(NFCContract.NFCEntry._ID));
        String uid = cursor.getString(cursor.getColumnIndexOrThrow(NFCContract.NFCEntry.COLUMN_NAME_UID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(NFCContract.NFCEntry.COLUMN_NAME_NAME));

        nfcItemName.setText(name + "   " + uid);

        // Delete button setup.
        nfcItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.dbHelper.deleteNFC(nfcId);
                MainActivity.nfcCursorAdapter.refreshNFCList(MainActivity.dbHelper.loadNFCs());
            }
        });
    }

    public void refreshNFCList(Cursor cursor) {
        getCursor().close();
        swapCursor(cursor);
        MainActivity.alarmCursorAdapter.refreshNFCListInSpinner(cursor);
    }
}
