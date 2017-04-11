/*
When the save contact button is pressed the AddContact class is called and uses the contactDBHelper
class to insert the contact from the form into the local sql database.
*/
package com.example.craig.ssgps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddContact extends AppCompatActivity {
    ContactDBHelper addContact;
    EditText editName, editNumber;
    String editPriority;
    Button saveSettings;
    String editPrio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        addContact = new ContactDBHelper(this);
        editName = (EditText)findViewById(R.id.name_text);
        editNumber = (EditText)findViewById(R.id.phone_number);

        final Spinner m_spinner = (Spinner) findViewById(R.id.priority_Spinner);
        m_spinner.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.prio_int)));

        m_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editPrio = ((TextView)view).getText().toString();
//                Toast.makeText(SettingsActivity.this,((TextView)view).getText().toString()+"",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                                editPriority);
                        if(isInserted == true) {
                            Toast.makeText(AddContact.this, "Contact Added", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(AddContact.this, "Contact not Added", Toast.LENGTH_LONG).show();
                        }
                        Intent i;
                        finish();
                    }

                }
        );
    }

}
