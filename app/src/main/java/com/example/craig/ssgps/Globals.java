/*
stores the global variables to be shared among activities
 */

package com.example.craig.ssgps;


public class Globals {
    private static Globals instance;

    private static String myLat;
    private static String myLon;
    private static boolean inDanger;
    private Globals(){}

    public void setLat(String l){
        Globals.myLat=l;
    }

    public void setLon(String l){
        Globals.myLon=l;
    }

    public String getLat(){
        return Globals.myLat;
    }

    public String getLon(){
        return Globals.myLon;
    }

    public void setDanger(boolean x){
        Globals.inDanger=x;
    }

    public boolean getStatus(){
        return Globals.inDanger;
    }

    public static synchronized Globals getInstance(){
        if(instance==null)
            instance=new Globals();

        return instance;
    }
}
