package com.example.craig.ssgps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.craig.ssgps.models.Contact;
import com.example.craig.ssgps.models.Model;
import com.example.craig.ssgps.models.Settings;
import com.example.craig.ssgps.models.Zones;

import java.util.ArrayList;

/**
 * Created by Mikkel on 12/04/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "ssgps.db";
    private final static int version = 1;
    private ArrayList<Model> models;


    public DBHelper(Context context) {
        super(context, DB_NAME, null, version);
        models = new ArrayList<>();
        models.add(new Contact());
        models.add(new Settings());
        models.add(new Zones());
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Model m : models){
            db.execSQL(m.getCreateSQL());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
