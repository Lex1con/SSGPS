/*
Activity tracks user location on map and handles the Safe Zone creation and monitoring.
 */
package com.example.craig.ssgps;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.craig.ssgps.models.Zones;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
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

public class LocationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, GoogleMap.OnMapLongClickListener, ResultCallback<Status> {

    private static final String TAG = "LocationActivity";

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    private GoogleApiClient mGoogleApiClient;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private boolean mLocationPermissionGranted;

    public List geofenceList;
    public List markerList;
    public List circleList;
    public List uwiFenceList;
    public List FenceDetails;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public int x = 1;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private android.location.Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private String uid;

    Zones zonesDB;

    Button saveFences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = getIntent().getStringExtra("uid");
        geofenceList = new ArrayList();
        markerList = new ArrayList();
        circleList = new ArrayList();
        uwiFenceList = new ArrayList();
        FenceDetails = new ArrayList();
        zonesDB = new Zones(new DBHelper(this));

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        } else {
            Log.d(TAG, "Saved Instance was not null");
        }
        setContentView(R.layout.activity_location);

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

        saveFences = (Button)findViewById(R.id.button3);
        saveFences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Save button clicked");
                saveUWIFences();
            }
        });
//        buildFences();

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected to Google Services");
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Play services connection suspended");
    }

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
                        new AlertDialog.Builder(LocationActivity.this)
                                .setTitle("Create Fence")
                                .setMessage("Would you like to Delete this Zone?")
                                .setNegativeButton(android.R.string.cancel,null)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LatLng latLng = marker.getPosition();
                                        int id = Integer.valueOf(marker.getTitle());
                                        Object r = marker.getTag();
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
        buildFences();

        mMap.setOnMapLongClickListener(this);


        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /*MAPS FUNCTIONS*/

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
//            addToGeoFence(temp,150);
//            handleGeoFence();
        }
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

        new AlertDialog.Builder(LocationActivity.this)
                .setTitle("Create Fence")
                .setMessage("Would you like to place a Safe Zone here?")
                .setNegativeButton(android.R.string.cancel,null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(LocationActivity.this)
                                .setTitle("Select a Radius size (in meters)")
                                .setItems(R.array.radius_options, new DialogInterface.OnClickListener(){
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

    /*GEOFENCE FUNCTIONS*/

    public void addToGeoFence(Location location, float radius){
        /*
        * Adds a Geofence to the list of fences in the system at a location and a given radius
        */

        Map details = new HashMap();
        details.put("latitude", location.getLatitude());
        details.put("longitude", location.getLongitude());
        details.put("radius", radius);
        details.put("ID",geofenceList.size());

        geofenceList.add(new Geofence.Builder()
                .setRequestId(String.valueOf(geofenceList.size()))
                .setCircularRegion(
                        location.getLatitude(),
                        location.getLongitude(),
                        radius
                )
                .setExpirationDuration(60000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        );
        FenceDetails.add(details);
        Log.d(TAG, "Size of Geofence list is now "+ geofenceList.size());
        Log.d(TAG, "Attempting to display fence");
    }
//
    public void deleteFence(Marker marker){
        /*
        Takes a marker that is associated with a GeoFence and uses it to delete the GeoFence from the system
         */

        Log.d(TAG, "Attempting to delete Fence");
        int id = Integer.valueOf(marker.getTitle());
        int listid = markerList.indexOf(marker);
        Log.d(TAG, "Fence ID:"+ id);
        Log.d(TAG, "List ID:"+ listid);

        geofenceList.remove(listid);
        marker.remove();
        markerList.remove(listid);
        Circle circle = (Circle) circleList.get(listid);
        circle.remove();
        circleList.remove(listid);
        Log.d(TAG, "Fence Deleted");
    }

    public void handleGeoFence(){

        /*
        handleGeoFence takes the list of fences and places them on the map and calls the GeoFence Services.
        */

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Attempting to setup Add a GeoFence");
            if (geofenceList != null &&  geofenceList.size() > 0) {

                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeofencingRequest(),
                        getGeofencePendingIntent()
                ).setResultCallback(this);
            }else{
                Log.d(TAG, "No values in the geoFence List");
                return;
            }
        }else{
            Log.d(TAG, "Permission for Location not given");
            return;
        }
        Log.d(TAG, "Geofence setup complete");

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
    /*END OF FENCE FUNCTIONS*/

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

    public void addRecord(LatLng latLng, int id, float rad){
        Log.d(TAG,"Adding record");
        Map map = new HashMap();
        String ID = String.valueOf(id);
        map.put("ID", id);
        map.put("latitude", latLng.latitude);
        map.put("longitude", latLng.longitude);
        map.put("Radius", rad);
        DatabaseReference logRef = database.getReference("Users");
        logRef.child(uid).child("Zones").child(ID).setValue(map);
        Log.d(TAG,"Record added");

    }

    public void saveToLocal(LatLng latlng, float radius){
        String lat = String.valueOf(latlng.latitude);
        String lon = String.valueOf(latlng.longitude);
        String rad = String.valueOf(radius);
        boolean isInserted = zonesDB.insertData(lat,lon, rad);
        if(isInserted == true){
            Toast.makeText(this, "Zones Saved Locally",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Zones not Saved Locally",Toast.LENGTH_LONG).show();
        }
    }

    public void generateFences(){
        Cursor data = zonesDB.getAllData();
        if(data.getCount() == 0){
            Toast.makeText(this, "You have no zones!",Toast.LENGTH_LONG).show();
        }
        else{
            int x = 0;
            while (data.moveToNext()){
                Map details = new HashMap();
                details.put("latitude", data.getDouble(1));
                details.put("longitude", data.getDouble(2));
                details.put("radius", data.getDouble(3));
                details.put("ID",x);
                FenceDetails.add(details);

                final MarkerOptions fenceMarker;
                fenceMarker = new MarkerOptions()
                        .draggable(true)
                        .position(new LatLng(data.getDouble(1),data.getDouble(2)) )
                        .title(String.valueOf(x));
                fenceMarker.snippet("Radius: "+data.getDouble(3)+" \n Latitude: "+fenceMarker.getPosition().latitude+"\n Longitude: "+fenceMarker.getPosition().longitude);
                Marker marker = mMap.addMarker(fenceMarker);
                marker.setTag(data.getDouble(3));

                CircleOptions circleOptions = new CircleOptions()
                        .center(fenceMarker.getPosition())
                        .fillColor(0x40ff0000)
                        .strokeColor(Color.TRANSPARENT)
                        .radius(data.getDouble(3))
                        .strokeWidth(2);
                Circle circle = mMap.addCircle(circleOptions);
                circleList.add(circle);
                x++;

            }
        }
        handleGeoFence();
    }

    public void saveUWIFences(){
        Log.d(TAG,"Saving Fences");
        for(int i = 0; i < FenceDetails.size(); i++){
            Map m = (Map) FenceDetails.get(i);
            int id = (int) m.get("ID");
            Log.d(TAG,"Fence: "+id+" saving");
            double lat = (double) m.get("latitude");
            double lon = (double) m.get("longitude");
            float radius = (float) m.get("radius");
            LatLng latlng = new LatLng(lat,lon);
            addRecord(latlng, id, radius);
            saveToLocal(latlng, radius);
            Log.d(TAG,"Fence: "+id+" saved");
        }
        Log.d(TAG,"Fences Saved");
    }

    void buildFences(){
        Log.d(TAG,"Building Your Zones");
        Cursor data = zonesDB.getAllData();
        if(data.getCount() == 0){
            Toast.makeText(this, "You have no safe zones",Toast.LENGTH_SHORT).show();
        }else{
            while (data.moveToNext()){
                Map details = new HashMap();
                details.put("latitude", data.getDouble(1));
                details.put("longitude",data.getDouble(2) );
                details.put("radius",data.getFloat(3));
                details.put("ID", data.getInt(0));
                FenceDetails.add(details);
//                geofenceList.add(new Geofence.Builder()
//                        .setRequestId(String.valueOf(details.get("ID")))
//                        .setCircularRegion(
//                                (double)details.get("latitude"),
//                                (double)details.get("longitude"),
//                                (float)details.get("radius")
//                        )
//                        .setExpirationDuration(-1)
//                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
//                        .build()
//                );
                final MarkerOptions fenceMarker;
                fenceMarker = new MarkerOptions()
                        .draggable(true)
                        .position(new LatLng(data.getDouble(1),data.getDouble(2)) )
                        .title(String.valueOf(x));
                fenceMarker.snippet("Radius: "+data.getFloat(3)+" \n Latitude: "+fenceMarker.getPosition().latitude+"\n Longitude: "+fenceMarker.getPosition().longitude);
                Marker marker = mMap.addMarker(fenceMarker);
                marker.setTag(data.getDouble(3));

                CircleOptions circleOptions = new CircleOptions()
                        .center(fenceMarker.getPosition())
                        .fillColor(0x40ff0000)
                        .strokeColor(Color.TRANSPARENT)
                        .radius(data.getDouble(3))
                        .strokeWidth(2);
                Circle circle = mMap.addCircle(circleOptions);
                circleList.add(circle);
            }
            handleGeoFence();
        }
    }

}
