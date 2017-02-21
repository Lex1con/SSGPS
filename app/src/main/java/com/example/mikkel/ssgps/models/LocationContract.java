package com.example.mikkel.ssgps.models;

import android.provider.BaseColumns;

/**
 * Created by M.hayes on 4/13/2016.
 */
public class LocationContract {
    public static final String CEATE_TABLE =
            "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                    LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    LocationEntry.LON + " REAL NOT NULL, " +
                    LocationEntry.LAT + " REAL NOT NULL );";


    public static abstract class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";
        public static final String LON = "longitude";
        public static final String LAT = "latitude";
    }
}
