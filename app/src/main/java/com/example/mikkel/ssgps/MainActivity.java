package com.example.mikkel.ssgps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static  final int MY_PERMISSION_CODE = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "Starting Process");
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);

        boolean hasPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean hasPermission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;

        if(!hasPermission){
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_CODE);
        }else{
            if(!isServiceSet())
                startBackgroundProcess();
        }

        if(!hasPermission2){
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.SEND_SMS},
                    MY_PERMISSION_CODE);
        }
//        else{
//            if(!isServiceSet())
//                startBackgroundProcess();
//        }

        ListView lv = (ListView)findViewById(R.id.menu_list);
        if(lv != null) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("itemid", position);
                    if(position == 0){
                        Intent i = new Intent(MainActivity.this, LocationActivity.class);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                    if(position == 1){
                        Intent i = new Intent(MainActivity.this, ZonesActivity.class);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                    if(position == 2){
                        Intent i = new Intent(MainActivity.this, SendSMSActivity.class);
                        i.putExtras(bundle);
                        startActivity(i);
                    }

                }
            });
        }
    }


    public boolean isServiceSet(){
        SharedPreferences pref = getSharedPreferences("DCIT", Context.MODE_PRIVATE);
        return pref.getBoolean("service_set", false);
    }

    public void startBackgroundProcess(){
//        Log.d("MainActivity", "Starting Process");
//        Intent intent = new Intent(this, LocationService.class);
//        startService(intent);
        LocationReciever.setUpService(this);
        SharedPreferences.Editor editor = getSharedPreferences("DCIT",Context.MODE_PRIVATE).edit();
        editor.putBoolean("service_set", true).apply();
    }

}
