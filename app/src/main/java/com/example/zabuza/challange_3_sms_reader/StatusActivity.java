package com.example.zabuza.challange_3_sms_reader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class StatusActivity extends AppCompatActivity {

    ArrayList<String> smsArrayAdressUnique = new ArrayList<String>();
    ListView adressListView;
    ArrayAdapter arrayAdapter;
    String mostFrequentString = "";
    TextView frequentStringTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        frequentStringTv = (TextView)findViewById(R.id.frequent_string);

        Intent intent = getIntent();
        smsArrayAdressUnique = intent.getStringArrayListExtra(Intent.EXTRA_TEXT);
        mostFrequentString = intent.getStringExtra("String");

        frequentStringTv.setText(mostFrequentString);

        adressListView = (ListView)findViewById(R.id.SMSadressList);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsArrayAdressUnique);
        adressListView.setAdapter(arrayAdapter);

    }
}
