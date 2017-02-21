package com.example.mikkel.ssgps;

/**
 * Created by M.hayes on 4/13/2016.
 */
public class Location {
    private int id;
    private double longitude;
    private double latitude;

    public Location(int id, double longitude, double latitude) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public String toString(){
        return id + " - ( " + latitude + "," + longitude + ")";
    }
}
