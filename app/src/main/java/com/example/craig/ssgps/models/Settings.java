package com.example.craig.ssgps.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.craig.ssgps.DBHelper;

/**
 * Created by Mikkel on 12/04/2017.
 */

public class Settings extends Model {
    private static final String TABLE_NAME = "settings";
    public static final String Setting_ID = "ID";
    public static final String CHECK_INTERVAL = "checks";
    public static final String REPORT_INTERVAL = "report";
    public static final String MAX_MISSED = "missed_checks";


    public Settings() {
    }

    public Settings(DBHelper helper) {
        super(helper);
    }

    @Override
    public String getCreateSQL() {
        return "create table "+TABLE_NAME+" (ID INTEGER PRIMARY KEY, checks INTEGER, report INTEGER, missed_checks INTEGER);";
    }

    public boolean insertData(String checks,String report,String missed_check) {
        SQLiteDatabase db = helper.getWritableDatabase();
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
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public boolean updateData(String checks,String report,String missed_check) {
        String id = "1";
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Setting_ID,id);
        contentValues.put(CHECK_INTERVAL,checks);
        contentValues.put(REPORT_INTERVAL,report);
        contentValues.put(MAX_MISSED,missed_check);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }
}
