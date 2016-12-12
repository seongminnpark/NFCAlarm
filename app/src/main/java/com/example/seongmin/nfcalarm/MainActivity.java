package com.example.seongmin.nfcalarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

        // Test data source.
        ArrayList<String> mockData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mockData.add("11:30 am");
        }

        ArrayAdapter<String> mockAdapter = new ArrayAdapter<>(this, R.layout.alarm_list_item, mockData);
        listView.setAdapter(mockAdapter);
    }
}
