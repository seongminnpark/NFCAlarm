package com.main.seongmin.nfcalarm;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
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
        TextView nfcItem = (TextView) view.findViewById(R.id.item_nfc);

        String uid = cursor.getString(cursor.getColumnIndexOrThrow(NFCContract.NFCEntry.COLUMN_NAME_UID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(NFCContract.NFCEntry.COLUMN_NAME_NAME));

        nfcItem.setText(name + "   " + uid);
    }

    public void refreshNFCList(Cursor cursor) {
        getCursor().close();
        swapCursor(cursor);
    }
}
