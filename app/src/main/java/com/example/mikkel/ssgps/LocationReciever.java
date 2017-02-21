package com.example.mikkel.ssgps;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by M.hayes on 4/13/2016.
 */
public class LocationReciever extends BroadcastReceiver {
    public  static final int INTERVAL = 5000;
    public static  final int REQUEST_CODE = 342;

    @Override
    public void onReceive(Context context, Intent intent){
        setUpService(context);
    }

    public static boolean setUpService(final Context context){
        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(context, LocationService.class);
        PendingIntent pi = PendingIntent.getService(context,REQUEST_CODE, i , 0);
        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0 , INTERVAL, pi);
        return true;
    }
}
