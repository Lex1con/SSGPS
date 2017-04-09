package com.example.mikkel.ssgps;

import android.Manifest;
import android.app.IntentService;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.health.ServiceHealthStats;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.mikkel.ssgps.models.DBHelper;
import com.example.mikkel.ssgps.models.LocationContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by M.hayes on 4/12/2016.
 */
public class LocationService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, CloseListener{

    private static final String NAME = "LocationService";
//    private Looper mServiceLooper;
//    private ServiceHandler mServiceHandler;
    Context context;
    public GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    protected ConnectivityManager connManager;
    protected NetworkInfo mWifi;
    protected LocationManager manager;
    protected LocationListener listener;
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
        //            Thread.sleep(5000);
//        Log.d("Location Service", "Sleeping");

        if(mWifi != null){
            if(mWifi.getType() == ConnectivityManager.TYPE_MOBILE){
                Log.d("LocationService","Connected to mobile data. Charges will be applied");
                Toast.makeText(context, "Connected to mobile data. Charges will be applied", Toast.LENGTH_SHORT).show();
            }else{
                Log.d("LocationService","Connected to WIFI. Charges will not be applied");
                Toast.makeText(context, "Connected to WIFI. Charges will not be applied", Toast.LENGTH_SHORT).show();
            }
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            mGoogleApiClient.connect();



        }
        else{
            listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Toast.makeText(LocationService.this, "Latitiude: " + location.getLatitude()+ " Longitude: "+location.getLongitude(), Toast.LENGTH_SHORT).show();

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
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Location Service", "Google Client connected");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Location service","No permission ");
        } else {
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Location Service", "Google Connection failed");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Location Service", "Connection Suspended");
    }

//    public void onLocationChanged(Location location){
//        Location mCurrentLocation = location;
//        String mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//    }

}
