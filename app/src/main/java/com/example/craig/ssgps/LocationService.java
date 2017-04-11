package com.example.craig.ssgps;

import android.*;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mikkel on 09/04/2017.
 */

public class LocationService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, CloseListener, ResultCallback<Status> {

    private static final String NAME = "LocationService";
    Location lastlocation;
    public GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest = LocationRequest.create();
    protected com.google.android.gms.location.LocationListener glistener;

    public List uwiFenceList;
    public List FenceDetails;

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
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 5, listener);

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
        mLocationRequest.setInterval(60000);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Location service","No permission Granted");

        } else {
            if (lastlocation == null) {
                Log.d("Location service","Location is NUll ");
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, glistener);
        }
        addFences();
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

    public void addFences(){
        uwiFenceList = new ArrayList();
        FenceDetails = new ArrayList();
        float radius = 150;
        Map details = new HashMap();
        details.put("latitude",10.643121452729275 );
        details.put("longitude",-61.40167124569416 );
        details.put("radius",150 );
        details.put("ID", 0);

        uwiFenceList.add(new Geofence.Builder()
                .setRequestId(String.valueOf(0))
                .setCircularRegion(
                        (double)details.get("latitude"),
                        (double)details.get("longitude"),
                        radius
                )
                .setExpirationDuration(-1)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        );

        FenceDetails.add(details);

        details = new HashMap();
        details.put("latitude",10.643922815731743 );
        details.put("longitude",-61.398724503815174 );
        details.put("radius",150 );
        details.put("ID", 1);

        uwiFenceList.add(new Geofence.Builder()
                .setRequestId(String.valueOf(1))
                .setCircularRegion(
                        (double)details.get("latitude"),
                        (double)details.get("longitude"),
                        radius
                )
                .setExpirationDuration(-1)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        );

        FenceDetails.add(details);

        details = new HashMap();
        details.put("latitude",10.641828130118515 );
        details.put("longitude",-61.39747995883227 );
        details.put("radius",150 );
        details.put("ID", 2);

        uwiFenceList.add(new Geofence.Builder()
                .setRequestId(String.valueOf(2))
                .setCircularRegion(
                        (double)details.get("latitude"),
                        (double)details.get("longitude"),
                        radius
                )
                .setExpirationDuration(-1)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        );

        FenceDetails.add(details);
        handleGeoFence();
    }

    public void handleGeoFence(){

        /*
        handleGeoFence takes the list of fences and places them on the map and calls the GeoFence Services.
        */

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("LocationService", "Attempting to setup Add a GeoFence");
            if (uwiFenceList != null &&  uwiFenceList.size() > 0) {

                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeofencingRequest(),
                        getGeofencePendingIntent()
                ).setResultCallback(this);
            }else{
                Log.d("LocationService", "No values in the geoFence List");
                return;
            }
        }else{
            Log.d("LocationService", "Permission for Location not given");
            return;
        }
        Log.d("LocationService", "Geofence setup complete");

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(uwiFenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }


    @Override
    public void onResult(@NonNull Status status) {

    }
}

