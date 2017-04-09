package com.example.craig.ssgps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    SettingsDBHelper settingsDB;
    EditText editCheck, editReport, editMissed;
    Button saveSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsDB = new SettingsDBHelper(this);
        editCheck = (EditText)findViewById(R.id.check_text);
        editReport = (EditText)findViewById(R.id.report_text);
        editMissed = (EditText)findViewById(R.id.miss_text);
        saveSettings = (Button)findViewById(R.id.settings_save);
//        AddData();
        UpdateData();
    }

    public  void AddData() {
        saveSettings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = settingsDB.insertData(editCheck.getText().toString(),
                                editReport.getText().toString(),
                                editMissed.getText().toString());
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
                        boolean isUpdate = settingsDB.updateData(editCheck.getText().toString(),
                                editReport.getText().toString(),
                                editMissed.getText().toString());
                        if(isUpdate == true)
                            Toast.makeText(SettingsActivity.this,"Settings Saved",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(SettingsActivity.this,"Settings not Saved",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
