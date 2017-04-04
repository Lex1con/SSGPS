package com.example.mikkel.ssgps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.*;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZonesActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "ZoneActivity";
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private GoogleApiClient mGoogleApiClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    public List geofenceList;
    public List markerList;
    public List circleList;
    public List uwiFenceList;

    public int x = 1;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private android.location.Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geofenceList = new ArrayList();
        markerList = new ArrayList();
        circleList = new ArrayList();

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        } else {
            Log.d("ZonesActivity", "Saved Instance was not null");
        }

        setContentView(R.layout.activity_zones);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        // Setup Google Services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        // Attempt to Connection to Google Services
        mGoogleApiClient.connect();
    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("ZoneActivity", "Connected to Google Services");
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                marker.setSnippet("Radius: 150 \n Latitude: "+marker.getPosition().latitude+"\n Longitude: "+marker.getPosition().longitude);
                marker.showInfoWindow();
            }
        });
//        mMap.setOnMarkerClickListener(new );

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {



                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {
                        new AlertDialog.Builder(ZonesActivity.this)
                                .setTitle("Create Fence")
                                .setMessage("Would you like to Make this Zone Permanent?")
                                .setNegativeButton(android.R.string.cancel,null)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        LatLng latLng = marker.getPosition();
//                                        int id = Integer.valueOf(marker.getTitle());
//                                        Object r = marker.getTag();
//                                        addRecord(latLng,id, (Integer) r);
                                        deleteFence(marker);
                                    }
                                })
                                .create()
                                .show();
                        return false;
                    }
                });



                return infoWindow;
            }


        });

        mMap.setOnMapLongClickListener(this);

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        Log.d(TAG, "Get Device Location executed");
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission Granted");
            mLocationPermissionGranted = true;
        } else {
            Log.d(TAG, "Permission not previously Granted requesting ");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            //Log.d(TAG, "Current location is not null attempting to create fence");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

            //addToGeoFence(mLastKnownLocation);
            //handleGeoFence();
        } else {
            Log.d(TAG, "Current location is null. No fence will be created.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            Location temp = new Location("");
            temp.setLatitude(mDefaultLocation.latitude);
            temp.setLongitude(mDefaultLocation.longitude);
            addToGeoFence(temp,150);
            handleGeoFence();
        }
    }

    public void addToGeoFence(Location location, float radius){

        geofenceList.add(new Geofence.Builder()
                .setRequestId(String.valueOf(geofenceList.size()))
                .setCircularRegion(
                        location.getLatitude(),
                        location.getLongitude(),
                        radius
                )
                .setExpirationDuration(60000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLoiteringDelay(30000)
                .build()
        );
        Log.d(TAG, "Size of Geofence list is now "+ geofenceList.size());
        Log.d("ZoneActivity", "Attempting to display fence");
//        MarkerOptions fenceMarker = new MarkerOptions()
//                .position(new LatLng(location.getLatitude(),location.getLongitude()) )
//                .title(String.valueOf(geofenceList.size()))
//                .snippet("Radius: 150 \n Latitude: "+location.getLatitude()+"\n Longitude: "+location.getLongitude());
//
//        mMap.addMarker( fenceMarker
//        ).showInfoWindow();
//
//        markerList.add(fenceMarker);
//
//        CircleOptions circleOptions = new CircleOptions()
//                .center(new LatLng(location.getLatitude(), location.getLongitude()))
//                .radius(150)
//                .fillColor(0x40ff0000)
//                .strokeColor(Color.TRANSPARENT)
//                .strokeWidth(2);

        //Circle circle = mMap.addCircle(circleOptions);
        //circleList.add(circle);
    }

    public void deleteFence(Marker marker){
        Log.d("ZoneActivity", "Attempting to delete Fence");
        int id = Integer.valueOf(marker.getTitle());
        int listid = markerList.indexOf(marker);
        Log.d("ZoneActivity", "Fence ID:"+ id);
        Log.d("ZoneActivity", "List ID:"+ listid);

        geofenceList.remove(listid);
        marker.remove();
        markerList.remove(listid);
        Circle circle = (Circle) circleList.get(listid);
        circle.remove();
        circleList.remove(listid);
        Log.d("ZoneActivity", "Fence Deleted");
    }

    public void handleGeoFence(){

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("ZoneActivity", "Attempting to setup Add a GeoFence");
            if (geofenceList != null &&  geofenceList.size() > 0) {

                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeofencingRequest(),
                        getGeofencePendingIntent()
                ).setResultCallback(this);
            }else{
                Log.d("ZoneActivity", "No values in the geoFence List");
                return;
            }
        }else{
            Log.d("ZoneActivity", "Permission for Location not given");
            return;
        }
        Log.d("ZoneActivity", "Geofence setup complete");

    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
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

    @Override
    protected void onPause(){
        super.onPause();
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                // This is the same pending intent that was used in addGeofences().
                getGeofencePendingIntent()
        ).setResultCallback(this); // Result processed in onResult().
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        final Location temp = new Location("");
        temp.setLatitude(latLng.latitude);
        temp.setLongitude(latLng.longitude);

        final MarkerOptions fenceMarker;
        fenceMarker = new MarkerOptions()
                .draggable(true)
                .position(new LatLng(temp.getLatitude(),temp.getLongitude()) )
                .title(String.valueOf(x));
        x++;
        fenceMarker.snippet("Radius: 150 \n Latitude: "+fenceMarker.getPosition().latitude+"\n Longitude: "+fenceMarker.getPosition().longitude);
        final Marker marker = mMap.addMarker( fenceMarker);

        new AlertDialog.Builder(ZonesActivity.this)
                .setTitle("Create Fence")
                .setMessage("Would you like to place a Safe Zone here?")
                .setNegativeButton(android.R.string.cancel,null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(ZonesActivity.this)
                                .setTitle("Select a Radius size")
                                .setItems(R.array.radius_sizes, new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CircleOptions circleOptions = new CircleOptions()
                                                .center(fenceMarker.getPosition())
                                                .fillColor(0x40ff0000)
                                                .strokeColor(Color.TRANSPARENT)
                                                .strokeWidth(2);

                                        if(which == 0){
                                            circleOptions.radius(100);
                                            marker.setTag(100);
                                            markerList.add(marker);
                                            addToGeoFence(temp,100);
                                            handleGeoFence();
                                        }
                                        if(which == 1){
                                            circleOptions.radius(150);
                                            marker.setTag(100);
                                            markerList.add(marker);
                                            addToGeoFence(temp,150);
                                            handleGeoFence();
                                        }
                                        if(which == 2){
                                            circleOptions.radius(200);
                                            marker.setTag(100);
                                            markerList.add(marker);
                                            addToGeoFence(temp,200);
                                            handleGeoFence();
                                        }


                                        Circle circle = mMap.addCircle(circleOptions);
                                        circleList.add(circle);
                                    }
                                })
                                .create()
                                .show();
                    }
                })
                .create()
                .show(); 

    }

    public void addRecord(LatLng latLng, int id, int rad){
        Map map = new HashMap();
        String ID = String.valueOf(id);
        map.put("ID", id);
        map.put("latitude", latLng.latitude);
        map.put("longitude", latLng.longitude);
        map.put("Radius", rad);
        DatabaseReference logRef = database.getReference("Fences");
        logRef.child("uwiFences").child(ID).setValue(map);
    }


}
