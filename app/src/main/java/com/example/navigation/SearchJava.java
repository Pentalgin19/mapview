package com.example.navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.widget.Toast;

public class SearchJava {

    private Location location;
    void setLocation(Location location1, Context context){
        location = location1;
    }

    public Location getLocation() {
        return location;
    }
    public Double getLat(Context context){
        SharedPreferences sp = context.getSharedPreferences("Location", Context.MODE_PRIVATE);
        Double lat = (double)sp.getFloat("lat", 0.000f);
        return lat;
    }
    public Double getLon(Context context){
        SharedPreferences sp = context.getSharedPreferences("Location", Context.MODE_PRIVATE);
        Double lon = (double)sp.getFloat("lon", 0.000f);
        return lon;
    }
}