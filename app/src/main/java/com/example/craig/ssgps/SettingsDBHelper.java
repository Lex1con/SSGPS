package com.example.craig.ssgps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SettingsDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "ssgps.db";
    public static final String TABLE_NAME = "settings_table";
    public static final String Setting_ID = "ID";
    public static final String CHECK_INTERVAL = "checks";
    public static final String REPORT_INTERVAL = "report";
    public static final String MAX_MISSED = "missed_checks";
    private final static int version = 2;

    public SettingsDBHelper(Context context) {
        super(context, DB_NAME, null, version);
//        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+" (ID INTEGER PRIMARY KEY, checks INTEGER, report INTEGER, missed_checks INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }

    public boolean insertData(String checks,String report,String missed_check) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHECK_INTERVAL,checks);
        contentValues.put(REPORT_INTERVAL,report);
        contentValues.put(MAX_MISSED,missed_check);
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

    public boolean updateData(String checks,String report,String missed_check) {
        String id = "1";
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Setting_ID,id);
        contentValues.put(CHECK_INTERVAL,checks);
        contentValues.put(REPORT_INTERVAL,report);
        contentValues.put(MAX_MISSED,missed_check);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }
}
