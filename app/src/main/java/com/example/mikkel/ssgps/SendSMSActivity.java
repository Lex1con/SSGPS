package com.example.mikkel.ssgps;

/**
 * Created by Che on 3/28/2017.
 */
import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendSMSActivity extends Activity {

    Button buttonSend;
    EditText textPhoneNo;
    EditText textSMS;
    String contact="8683317613";
    String msg="Latitude: Longitude: ";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("SMSSctivity", "SMS activity created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        //buttonSend = (Button) findViewById(R.id.buttonSend);
        //textPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
        //textSMS = (EditText) findViewById(R.id.editTextSMS);

        //send message on activity load-----------------------
        String phoneNo = contact;
        String sms = msg;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(),
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        //-----------------------------------------------------


//        buttonSend.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                String phoneNo = textPhoneNo.getText().toString();
//                String sms = textSMS.getText().toString();
//                String phoneNo = contact;
//                String sms = msg;
//                try {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(phoneNo, null, sms, null, null);
//                    Toast.makeText(getApplicationContext(), "SMS Sent!",
//                            Toast.LENGTH_LONG).show();
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(),
//                            "SMS faild, please try again later!",
//                            Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//                }
//
//            }
//        });
    }
}
