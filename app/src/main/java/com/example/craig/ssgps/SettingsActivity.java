/*
Handles all of the user's local preferences.
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

public class SettingsActivity extends AppCompatActivity {
    SettingsDBHelper settingsDB;
    String editCheck, editReport, editMissed;
    Button saveSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsDB = new SettingsDBHelper(this);

        final Spinner c_spinner = (Spinner) findViewById(R.id.check_Spinner);
        c_spinner.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.check_int)));

        c_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editCheck = ((TextView)view).getText().toString();
//                Toast.makeText(SettingsActivity.this,((TextView)view).getText().toString()+"",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner r_spinner = (Spinner) findViewById(R.id.report_Spinner);
        r_spinner.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.report_int)));

        r_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editReport = ((TextView)view).getText().toString();
//                Toast.makeText(SettingsActivity.this,((TextView)view).getText().toString()+"",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner m_spinner = (Spinner) findViewById(R.id.missed_Spinner);
        m_spinner.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.missed_int)));

        m_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editMissed = ((TextView)view).getText().toString();
//                Toast.makeText(SettingsActivity.this,((TextView)view).getText().toString()+"",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveSettings = (Button)findViewById(R.id.settings_save);
        UpdateData();
//        AddData();
    }

    public  void AddData() {
        saveSettings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = settingsDB.insertData(editCheck, editReport, editMissed);
                        if(isInserted == true) {
                            Toast.makeText(SettingsActivity.this, "Settings Saved", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(SettingsActivity.this, "Settings not Saved", Toast.LENGTH_LONG).show();
                        }
                        Intent i;

                    }
                }
        );
    }

    public void UpdateData() {
        saveSettings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isUpdate = settingsDB.updateData(editCheck, editReport, editMissed);
                        if(isUpdate == true)
                            Toast.makeText(SettingsActivity.this,"Settings Saved",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(SettingsActivity.this,"Settings not Saved",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
