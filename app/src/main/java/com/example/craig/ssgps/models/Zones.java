package com.example.craig.ssgps.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.example.craig.ssgps.DBHelper;

/**
 * Created by Mikkel on 12/04/2017.
 */

public class Zones extends Model{
    private static final String TABLE_NAME = "zones";
    public static final String Zone_Lat = "latitude";
    public static final String Zone_Lon = "longitude";
    public static final String Zone_Rad = "radius";
    public static final String Zone_ID = "id";

    public Zones(){
        super();
    }

    public Zones(DBHelper helper){
        super(helper);
    }

    @Override
    public String getCreateSQL() {
        return "CREATE TABLE "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, latitude REAL NOT NULL, longitude REAL NOT NULL, radius REAL );";
    }

    public boolean insertData(String lat,String lon, String radius) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Zone_Lat,lat);
        contentValues.put(Zone_Lon,lon);
        contentValues.put(Zone_Rad,radius);
        long result = db.insert(TABLE_NAME, null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        return res;
    }

    public boolean updateData(String id,double lat,double lon,float radius) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Zone_ID,id);
        contentValues.put(Zone_Lat,lat);
        contentValues.put(Zone_Lon,lon);
        contentValues.put(Zone_Rad,radius);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public void deleteData (Location location) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE "+Zone_Lat+" = "+location.getLatitude()+" AND "+Zone_Lon+" = "+location.getLongitude());
    }
}
