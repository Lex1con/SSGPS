package com.example.craig.ssgps;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String email;
    private String name;
    private String uid;

    SettingsDBHelper settingsDB;
    SettingsDBHelper contactsDB;

    private static  final int MY_PERMISSION_CODE = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "Starting Process");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean hasPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(!hasPermission){
            ActivityCompat.requestPermissions(this,
                    new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_CODE);
        }
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");
        uid = getIntent().getStringExtra("uid");

        settingsDB = new SettingsDBHelper(this);
        contactsDB = new SettingsDBHelper(this);

        Cursor s = settingsDB.getAllData();

        if(s.getCount() == 0){
            settingsDB.insertData("5","5","3");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                ((TextView)drawerView.findViewById(R.id.email)).setText(email);
                ((TextView)drawerView.findViewById(R.id.name)).setText(name);
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Globals g = Globals.getInstance();
        g.setDanger(false);

        boolean hasPermission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;

        if(!hasPermission2){//sms permissions
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.SEND_SMS},
                    MY_PERMISSION_CODE);
        }
        Help helpFragment = new Help();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(
                R.id.relative_layout,
                helpFragment,
                helpFragment.getTag()
        ).commit();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_locationActivity) {
            Intent intent;
            intent = new Intent(MainActivity.this, LocationActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_contactsActivity) {
            Intent intent;
            intent = new Intent(MainActivity.this, ContactsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settingsActivity) {
            Intent intent;
            intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }   else if (id == R.id.nav_firebaseActivity) {
            Intent intent;
            intent = new Intent(MainActivity.this, FireBaseActivity.class);
            intent.putExtra("uid",uid);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
