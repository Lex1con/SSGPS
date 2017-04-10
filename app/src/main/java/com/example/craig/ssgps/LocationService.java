package com.example.craig.ssgps;

import android.*;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Mikkel on 09/04/2017.
 */

public class LocationService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, CloseListener{

    private static final String NAME = "LocationService";
    Location lastlocation;
    public GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest = LocationRequest.create();
    protected com.google.android.gms.location.LocationListener glistener;

    protected ConnectivityManager connManager;
    protected NetworkInfo mWifi;


    protected LocationListener listener;
    protected LocationManager manager;

    private boolean hasPermission = false;

//    SQLiteOpenHelper helper = new DBHelper(this);
//    final SQLiteDatabase db = helper.getWritableDatabase();

    public LocationService(){super(NAME);}

    @Override
    protected void onHandleIntent(Intent intent){
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getActiveNetworkInfo();
        Log.d("LocationService","Handling intent for service");

        if(mWifi != null){
            if(mWifi.getType() == ConnectivityManager.TYPE_MOBILE){
                Log.d("LocationService","Connected to mobile data. Charges will be applied");
//                Toast.makeText(LocationService.this, "Connected to mobile data. Charges will be applied", Toast.LENGTH_SHORT).show();
            }else{
                Log.d("LocationService","Connected to WIFI. Charges will not be applied");
//                Toast.makeText(LocationService.this, "Connected to WIFI. Charges will not be applied", Toast.LENGTH_SHORT).show();
            }
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            glistener = new com.google.android.gms.location.LocationListener(){
                @Override
                public void onLocationChanged(Location location) {
                    Log.d("Location Service"," New location: Latitiude: " + location.getLatitude()+ " Longitude: "+location.getLongitude());
//                    Toast.makeText(getApplicationContext(), "Latitiude: " + location.getLatitude()+ " Longitude: "+location.getLongitude(), Toast.LENGTH_SHORT).show();
                    lastlocation.set(location);
                }
            };
            mGoogleApiClient.connect();
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, glistener);




        }
        else{
            Log.d("LocationService","No internet connection, Using Device GPS");
            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    Log.d("Location Service"," New location: Latitiude: " + location.getLatitude()+ " Longitude: "+location.getLongitude());
//                    Toast.makeText(getApplicationContext(), "Latitiude: " + location.getLatitude()+ " Longitude: "+location.getLongitude(), Toast.LENGTH_SHORT).show();
                    lastlocation.set(location);
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
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(NAME, "Unable to access GPS");
            } else {
                hasPermission = true;
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, listener);

            }
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void deRegisterListener(){
        if (manager != null && listener != null){
            if(hasPermission)
                manager.removeUpdates(listener);
        }
        if (manager != null && glistener != null){
            if(hasPermission)
                manager.removeUpdates(listener);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Location Service", "Google Client connected");
        lastlocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mLocationRequest.setInterval(500);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Location service","No permission Granted");

        } else {
            if (lastlocation == null) {
                Log.d("Location service","Location is NUll ");
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, glistener);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Location Service", "Google Connection failed");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Location Service", "Connection Suspended");
        manager.removeUpdates(listener);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, glistener);

    }
//    public void onLocationChanged(Location location){
//        Log.d("Location Service"," New location: "+ location);
//        lastlocation.set(location);
//
//    }



}

