package com.example.craig.ssgps.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.craig.ssgps.DBHelper;
import com.example.craig.ssgps.SingleItem;

/**
 * Created by Mikkel on 12/04/2017.
 */

public class Contact extends Model {
    private static final String TABLE_NAME = "contacts";
    public static final String Contact_ID = "ID";
    public static final String Contact_NUM = "number";
    public static final String Contact_NAME = "name";
    public static final String Contact_Priority = "priority";

    public Contact() {
    }

    public Contact(DBHelper helper) {
        super(helper);
    }

    @Override
    public String getCreateSQL() {
        return "create table "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, number INTEGER, priority INTEGER)";
    }

    public boolean insertData(String number,String name,String priority) {
        SQLiteDatabase db = helper.getWritableDatabase();
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
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        return res;
    }

    public boolean updateData(String id, String number,String name,String priority) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("UPDATE "+TABLE_NAME+" SET name = '"+name+"', number = '"+number+"', priority ='"+priority+"' WHERE ID = "+id+";");
        return true;
    }

    public void deleteData(SingleItem name){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE from "+TABLE_NAME+" WHERE "+Contact_ID+" = "+name.getId());
    }
}
