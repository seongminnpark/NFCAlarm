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
public class NFCListFragment extends Fragment {

    private ListView nfcListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nfc_list, container, false);

        nfcListView = (ListView) rootView.findViewById(R.id.nfcListView);
        nfcListView.setAdapter(MainActivity.nfcCursorAdapter);
        nfcListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Cursor nfcCursor = MainActivity.nfcCursorAdapter.getCursor();
                nfcCursor.moveToPosition(pos);
                String nfcId = nfcCursor.getString(nfcCursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry._ID));
                MainActivity.dbHelper.deleteAlarm(nfcId);
                MainActivity.alarmReceiver.cancelAlarm(getActivity(), Integer.parseInt(nfcId));
                MainActivity.nfcCursorAdapter.refreshNFCList(MainActivity.dbHelper.loadNFCs());
            }
        });

        return rootView;
    }
}
