package com.main.seongmin.nfcalarm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        return rootView;
    }
}
