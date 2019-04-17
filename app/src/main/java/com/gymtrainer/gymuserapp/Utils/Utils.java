package com.gymtrainer.gymuserapp.Utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class Utils
{
    public static boolean isTrainerNearby(LatLng currentLatLng,LatLng trainerLatLng)
    {
        float[] results = new float[1];
        Location.distanceBetween(currentLatLng.latitude, currentLatLng.longitude, trainerLatLng.latitude, trainerLatLng.longitude, results);
        float distanceInMeters = results[0];
        boolean isWithin10km = distanceInMeters < 30000;
        return isWithin10km;

    }
}
