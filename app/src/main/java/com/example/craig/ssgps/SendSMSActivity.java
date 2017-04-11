/*
When the Help fragment is created this class is called and attempts to send the gps location alert
to all the contacts specified by the user.
 */

package com.example.craig.ssgps;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;


public class SendSMSActivity extends AppCompatActivity {


    Globals g = Globals.getInstance();

    String msg="User felt unsafe at https://www.google.tt/maps/@:"+g.getLat() +","+g.getLon()+"z";
    ContactDBHelper contactsDB;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("SMSSctivity", "SMS activity created");
        super.onCreate(savedInstanceState);

        contactsDB = new ContactDBHelper(this);
        Cursor data = contactsDB.getAllData();
        ArrayList<SingleItem> theList = new ArrayList<>();
        int size = data.getCount();
        if(data.getCount() == 0){
            Toast.makeText(this, "There are no contents in this list!",Toast.LENGTH_LONG).show();
        }else {
            while (data.moveToNext()) {
                theList.add(new SingleItem(data.getString(1),data.getInt(0),data.getInt(2),data.getInt(3)));

            }
            for(int i=0; i<size; i++){//loops through all contacts in local database and send sms msg to all
                SingleItem item = theList.get(i);
                Log.d("SMSSctivity", "i="+i+" "+item.getNumber());
//                if(item.getPriority() == 1) {
                    //Toast.makeText(getApplicationContext(), "Alert" + msg + " Sent! to" + item.getNumber(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Messages Sent to Contacts", Toast.LENGTH_LONG).show();
                    String phoneNo = String.valueOf(item.getNumber());
                    String sms = msg;

                    try {//attempt to send message
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                        Toast.makeText(getApplicationContext(), "Alert"+msg+" Sent! to"+theList.get(i).getNumber(),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {

                        Toast.makeText(getApplicationContext(),
                                e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
//                }
            }
        }



        finish();

    }
}
