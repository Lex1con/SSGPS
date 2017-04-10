package com.example.craig.ssgps;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ContactDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "ssgps.db";
    public static final String TABLE_NAME = "contacts_table";
    public static final String Contact_ID = "ID";
    public static final String Contact_NUM = "number";
    public static final String Contact_NAME = "name";
    public static final String Contact_Priority = "priority";
    SQLiteDatabase db = this.getWritableDatabase();
    private final static int version = 2;


    public ContactDBHelper(Context context) {
        super(context, DB_NAME, null, version);
//        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, number INTEGER, priority INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }


    public boolean insertData(String number,String name,String priority) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contact_NUM,number);
        contentValues.put(Contact_NAME,name);
        contentValues.put(Contact_Priority,priority);
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

    public boolean updateData(String id,String number,String name,String priority) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contact_ID,id);
        contentValues.put(Contact_NUM,number);
        contentValues.put(Contact_NAME,name);
        contentValues.put(Contact_Priority,priority);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public void deleteData(SingleItem name){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE from "+TABLE_NAME+" WHERE "+Contact_ID+" = "+name.getId());
    }

    public void updateData(SingleItem item) {
    }
}
