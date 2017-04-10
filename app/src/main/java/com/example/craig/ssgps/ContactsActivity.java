package com.example.craig.ssgps;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {
    ContactDBHelper contactsDB;
    Button addContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        ListView listView = (ListView) findViewById(R.id.list_contacts);
        contactsDB = new ContactDBHelper(this);


        //populate an ArrayList<String> from the database and then view it
        ArrayList<SingleItem> theList = new ArrayList<>();
        Cursor data = contactsDB.getAllData();
        if(data.getCount() == 0){
            Toast.makeText(this, "There are no contents in this list!",Toast.LENGTH_LONG).show();
        }else {
            while (data.moveToNext()) {
                theList.add(new SingleItem(data.getString(1),data.getInt(0),data.getInt(2),data.getInt(3)));
                ContactList_Custom adapter = new ContactList_Custom(theList,this,this);
                listView.setAdapter(adapter);
            }
        }



        addContact = (Button)findViewById(R.id.new_contact);
        addContact.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        intent = new Intent(ContactsActivity.this, AddContact.class);
//                      intent.putExtra();
                        startActivity(intent);
                    }
                }

        );

    }

}
