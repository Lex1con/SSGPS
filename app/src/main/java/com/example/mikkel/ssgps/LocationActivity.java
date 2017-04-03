package com.example.mikkel.ssgps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.*;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LocationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    protected LocationManager manager;
    protected LocationListener listener;
    public String dLat="0";
    public String dLong="0";

    private boolean hasPermission = false;
    String user = "Mikkel";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");


    public GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        myRef.setValue("Hello, World!");



    }

    protected void onStart(){
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(LocationActivity.this, "No permission ", Toast.LENGTH_SHORT).show();
        } else {
            hasPermission = true;
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Toast.makeText(LocationActivity.this, "Latitude : " + mLastLocation.getLatitude() + "\n Longitude : "+ mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            }
        }
    }

    public void getLastGooglePlay(View view){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(LocationActivity.this, "No permission ", Toast.LENGTH_SHORT).show();
        } else {
            hasPermission = true;
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                SimpleDateFormat tdf=new SimpleDateFormat("HH:mm aa");
                SimpleDateFormat ddf=new SimpleDateFormat("dd-MM-yyyy");
                final String date = ddf.format(Calendar.getInstance().getTime());
                final String time = tdf.format(Calendar.getInstance().getTime());
                final String lat = String.valueOf(mLastLocation.getLatitude());
                final String lon = String.valueOf(mLastLocation.getLongitude());
                dLat=lat;
                dLong=lon;
                addRecord(date,time,lat,lon);
                Toast.makeText(LocationActivity.this, "Latitude : " + mLastLocation.getLatitude() + "\n Longitude : "+ mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            }
        }
    }

    public void displayLocation(View view){
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                //Toast.makeText(LocationActivity.this, "Latitiude: " + location.getLatitude()+ " Longitude: "+location.getLongitude(), Toast.LENGTH_SHORT).show();
                new Thread(){
                    public void run(){
                        LocationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LocationActivity.this, "Latitiude: " + location.getLatitude()+ " Longitude: "+location.getLongitude(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }.start();


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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(LocationActivity.this, "No permission ", Toast.LENGTH_SHORT).show();
        } else {
            hasPermission = true;
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, listener);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.removeUpdates(listener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//    }

    public void addRecord(String date, String time, String lat, String lon){
        Map map = new HashMap();
        //map.put("uID", "Mikkel");
        map.put("latitude", lat);
        map.put("longitude", lon);
        DatabaseReference logRef = database.getReference("logs");
        logRef.child(user).child(date).child(time).setValue(map);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
