/*
Handles all interactions between the safe zone table in the database.
 */
package com.example.craig.ssgps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

/**
 * Created by Mikkel on 10/04/2017.
 */

public class ZoneDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "ssgps.db";
    public static final String TABLE_NAME = "zones_table";
    public static final String Zone_ID = "ID";
    public static final String Zone_Lat = "latitude";
    public static final String Zone_Lon = "longitude";
    public static final String Zone_Rad = "radius";
    SQLiteDatabase db = this.getWritableDatabase();
    private final static int version = 2;

    public ZoneDBHelper(Context context) {
        super(context, DB_NAME, null, version);
//        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, latitude REAL NOT NULL, longitude REAL NOT NULL, radius REAL)");
        Log.d("ZoneDatabase","Table Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST "+TABLE_NAME);
        onCreate(db);
    }


    public boolean insertData(String lat,String lon, String radius) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Zone_Lat,lat);
        contentValues.put(Zone_Lon,lon);
        contentValues.put(Zone_Rad,radius);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public boolean updateData(String id,double lat,double lon,float radius) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Zone_ID,id);
        contentValues.put(Zone_Lat,lat);
        contentValues.put(Zone_Lon,lon);
        contentValues.put(Zone_Rad,radius);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public void deleteData (Location location) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE from "+TABLE_NAME+" WHERE "+Zone_Lat+" = "+location.getLatitude()+" AND "+Zone_Lon+" = "+location.getLongitude());
    }
}
