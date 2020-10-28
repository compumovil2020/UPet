package com.example.proyectoupet.services.MapsServices;

import android.app.Activity;
import android.util.Log;

import com.example.proyectoupet.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlacesService {

    public void findPlaces(Activity activeActivity, GoogleMap mMap, LatLng origin, List<String> placeTypes, List<Marker> markers){
        String uri = getRequestedUrl(activeActivity,origin,placeTypes);
        new TaskPlacesRequest(mMap,markers).execute(uri);
    }

    private String getRequestedUrl(Activity activeActivity, LatLng origin, List<String> placeType) {
        String strOrigin = "location=" + origin.latitude + "," + origin.longitude;
        String radius = "radius=5000";
        String sensor = "sensor=true";
        String types = "type="+placeType.stream().collect(Collectors.joining(","));

        String param = strOrigin + "&" + radius + "&" + sensor + "&" + types;
        String output = "json";
        String APIKEY = activeActivity.getResources().getString(R.string.google_maps_key);

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/" + output + "?" + param + "&key="+APIKEY;
        Log.d("TAG", url);
        return url;
    }
}
