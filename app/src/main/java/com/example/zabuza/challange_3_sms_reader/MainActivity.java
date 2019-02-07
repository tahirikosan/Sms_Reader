package com.example.zabuza.challange_3_sms_reader;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.renderscript.Sampler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_TEXT = "Array";
    public static final String EXTRA_STRING = "String";

    private static MainActivity inst;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ArrayList<String> smsAdresses = new ArrayList<String>();
    ArrayList<String> smsBody = new ArrayList<String>();
    HashSet<String> smsAdresses_unique = new HashSet<>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;
    String mostFrequentString = "";

    Button status_btn;

    public static MainActivity instance() {
        return inst;
    }
    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smsListView = (ListView) findViewById(R.id.SMSList);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        smsListView.setAdapter(arrayAdapter);

        status_btn = (Button)findViewById(R.id.status_btn);

        status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StatusActivity.class);
                smsAdresses = new ArrayList<String>(smsAdresses_unique); //Hashmap olan smsAdress_unique yi ArrayLİste çevirmemiz gerek.Status activity de listview da kullanmak için lazım.
                intent.putExtra(Intent.EXTRA_TEXT, smsAdresses);
                intent.putExtra("String", mostFrequentString);
                startActivity(intent);
            }
        });

        // Add SMS Read Permision At Runtime
        // Todo : If Permission Is Not GRANTED
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {

            // Todo : If Permission Granted Then Show SMS
            refreshSmsInbox();
           smsAdresses_unique = smsAdressArrayMaker(smsAdresses);
           mostFrequentString = mostFrequentWord(smsBody);

        } else {
            // Todo : Then Set Permission
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        System.out.println(mostFrequentString);
    }

    public String mostFrequentWord(ArrayList<String> smsBody){

        ArrayList<String> tmpArrayList = new ArrayList<>();
        for(int i = 0; i < smsBody.size(); i++){ //To concat every paragraf in array.
            String [] tmpArray  = smsBody.get(i).split("\\s+");
            for(int j = 0; j < tmpArray.length; j++){
                tmpArrayList.add(tmpArray[j]);
            }
        }
        String tmp = "";
        int big_number = 0;
        int tmp_number = 0;

        for(int i = 0; i < tmpArrayList.size();i++){
            for(int j = 0; j < tmpArrayList.size();j++){
                if(tmpArrayList.get(i).equals(tmpArrayList.get(j))){
                    tmp_number++;
                }
            }
            if(tmp_number > big_number){
                big_number = tmp_number;
                tmp = tmpArrayList.get(i);
            }
            tmp_number = 0;
        }

        return "----> "+ tmp +" <---- word is passing " +big_number + " times.";
    }

    public HashSet smsAdressArrayMaker(ArrayList<String> arrayList){

        ArrayList<String> smsAdresses_unique = new ArrayList<String>();
        int number = 0;
        for(int i = 0; i < arrayList.size();i++){
            for(int j = 0; j < arrayList.size() ;j++){
                if(arrayList.get(i).equals( arrayList.get(j))){
                    number++;
                }
            }
            smsAdresses_unique.add("From " + arrayList.get(i) + " have " + number + " messages.");
            number = 0;
        }

        HashSet<String> uniqueValues = new HashSet(smsAdresses_unique);
        return uniqueValues;
    }


    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        do {
            String adres = smsInboxCursor.getString(indexAddress);
            smsAdresses.add(adres);
            String body = smsInboxCursor.getString(indexBody);
            smsBody.add(body);

            String str = "SMS From: "+ smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
    }

    public void updateList(final String smsMessage) {
        arrayAdapter.insert(smsMessage, 0);
        arrayAdapter.notifyDataSetChanged();
    }

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        try {
            String[] smsMessages = smsMessagesList.get(pos).split("\n");
            String address = smsMessages[0];
            String smsMessage = "";
            for (int i = 1; i < smsMessages.length; ++i) {
                smsMessage += smsMessages[i];
            }

            String smsMessageStr = address + "\n";
            smsMessageStr += smsMessage;
            Toast.makeText(this, smsMessageStr, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Todo : Thanks For Watching...
    }

}