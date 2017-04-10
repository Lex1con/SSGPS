package com.example.craig.ssgps;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FireBaseActivity extends AppCompatActivity {
    ContactDBHelper contactsDB;
    SettingsDBHelper settingsDB;
    Button firebaseUp;
    Button firebaseDl;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");

    private String email;
    private String name;
    private String uid;

    ArrayList<String> contactList;
    ArrayList<String> settingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base);

        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");
        uid = getIntent().getStringExtra("uid");
        contactsDB = new ContactDBHelper(this);
        settingsDB = new SettingsDBHelper(this);
        firebaseUp = (Button)findViewById(R.id.firebase_Up);
        firebaseDl = (Button)findViewById(R.id.firebase_Dl);

        settingsList = new ArrayList<>();
        Cursor settingsData = settingsDB.getAllData();

        contactList = new ArrayList<>();
        Cursor contactData = contactsDB.getAllData();

        if(contactData.getCount() == 0){
            Log.d("List","empty");
        }else {
            while (contactData.moveToNext()) {
                contactList.add(contactData.getString(1));
            }
        }

        if(settingsData.getCount() == 0){
            Log.d("List","empty");
        }else {
            while (settingsData.moveToNext()) {
                settingsList.add(contactData.getString(1));
            }
        }

    }

    public void pushToFirebase() {
        firebaseUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                 e       addRecord();
                    }
                }
        );
    }

    public void addRecord(String name, String number, String priority){
        Map map = new HashMap();
        map.put("C_name", name);
        map.put("C_number", number);
        map.put("C_priority", priority);
        DatabaseReference logRef = database.getReference("logs");
        logRef.child(uid).child("Contacts").setValue(map);

    }

}
