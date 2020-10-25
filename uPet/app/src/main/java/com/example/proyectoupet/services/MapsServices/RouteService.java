package com.example.proyectoupet.services.MapsServices;

import android.app.Activity;
import android.util.Log;

import com.example.proyectoupet.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.Map;

public class RouteService {

    public void makeRoute(Activity activeActivity, GoogleMap mMap, LatLng origin, LatLng destination, Map<String, Polyline> polylines){
        String uri = getRequestedUrl(activeActivity,origin,destination);
        new TaskDirectionRequest(mMap,origin,destination,polylines).execute(uri);
    }

    private String getRequestedUrl(Activity activeActivity, LatLng origin, LatLng destination) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String param = strOrigin + "&" + strDestination + "&" + sensor + "&" + mode;
        String output = "json";
        String APIKEY = activeActivity.getResources().getString(R.string.google_maps_key);

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key="+APIKEY;
        Log.d("TAG", url);
        return url;
    }

}
