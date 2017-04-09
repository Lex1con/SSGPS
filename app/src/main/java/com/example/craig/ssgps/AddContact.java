package com.example.craig.ssgps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddContact extends AppCompatActivity {
    ContactDBHelper addContact;
    EditText editName, editNumber, editPriority;
    Button saveSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        addContact = new ContactDBHelper(this);
        editName = (EditText)findViewById(R.id.name_text);
        editPriority = (EditText)findViewById(R.id.priority_num);
        editNumber = (EditText)findViewById(R.id.phone_number);
        saveSettings = (Button)findViewById(R.id.save_contact);
        AddData();
    }
    public  void AddData() {
        saveSettings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(AddContact.this, "Button Pressed", Toast.LENGTH_LONG).show();
                        boolean isInserted = addContact.insertData(editNumber.getText().toString(),
                                editName.getText().toString(),
                                editPriority.getText().toString());
                        if(isInserted == true) {
                            Toast.makeText(AddContact.this, "Contact Added", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(AddContact.this, "Contact not Added", Toast.LENGTH_LONG).show();
                        }
                        Intent i;

                    }
                }
        );
    }

}
