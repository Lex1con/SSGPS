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

        ArrayList<SingleItem> contact_List = new ArrayList<>();
        Cursor data = contactsDB.getAllData();
        if(data.getCount() == 0){
            Toast.makeText(this, "There are no contents in this list!",Toast.LENGTH_LONG).show();
        }else {
            while (data.moveToNext()) {
                contact_List.add(new SingleItem(data.getString(1),data.getInt(0),data.getInt(2),data.getInt(3)));
            }
        }

        pushToFirebase();

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
