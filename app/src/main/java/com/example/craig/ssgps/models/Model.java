package com.example.craig.ssgps.models;

import com.example.craig.ssgps.DBHelper;

/**
 * Created by Mikkel on 12/04/2017.
 */

public abstract class Model {
    public DBHelper helper;

    public Model(){}

    public Model(DBHelper helper){
        this.helper = helper;
    }

    public abstract String getCreateSQL();
}
