package com.example.mikkel.ssgps;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.mikkel.ssgps.models.DBHelper;
import com.example.mikkel.ssgps.models.LocationContract;

/**
 * Created by M.hayes on 4/12/2016.
 */
public class LocationService extends IntentService implements CloseListener{

    private static final String NAME = "LocationService";
    Context context;

    protected LocationManager manager;
    protected LocationListener listener;
    private boolean hasPermission = false;

    SQLiteOpenHelper helper = new DBHelper(this);
    final SQLiteDatabase db = helper.getWritableDatabase();

    public LocationService(){super(NAME);}

    @Override
    protected void onHandleIntent(Intent intent){
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Double lon = location.getLongitude();
                Double lat = location.getLatitude();
                final String sql=
                        "INSERT INTO " + LocationContract.LocationEntry.TABLE_NAME +
                                " ("+ LocationContract.LocationEntry._ID + "," + LocationContract.LocationEntry.LON +
                                "," + LocationContract.LocationEntry.LAT + ") VALUES " +
                                "(null, "+ lon.toString() +", "+ lat.toString() +")";
                db.execSQL(sql);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.d(NAME, "Unable to access GPS");
        }else{
            hasPermission = true;
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, listener);

        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void deRegisterListener(){
        if (manager != null && listener != null){
            if(hasPermission)
                manager.removeUpdates(listener);
        }
    }

}
