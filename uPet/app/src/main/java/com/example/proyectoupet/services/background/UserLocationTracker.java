package com.example.proyectoupet.services.background;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.proyectoupet.services.MapsServices.MapService;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserLocationTracker extends Service {

    public static final String PATH_USER = "users/";
    FirebaseAuth firebaseAuth;
    LocationManager locationManager;
    LocationListener locationListener;
    FirebaseUser firebaseUser;
    MapService mapService;

    public UserLocationTracker() {
        super();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mapService = new MapService(null);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null) {
                    CollectionReference firebaseFirestore = FirebaseFirestore.getInstance().collection("ubicacion");
                    String locationName = mapService.getLatLngName(UserLocationTracker.this, new LatLng(location.getLatitude(), location.getLongitude()));
                    Map<String, Object> locationInfo = new HashMap<>();
                    locationInfo.put("latitud", location.getLatitude());
                    locationInfo.put("longitud", location.getLongitude());
                    locationInfo.put("nombre", locationName);
                    firebaseFirestore.document(firebaseAuth.getUid()).set(locationInfo);
//                    System.out.println("!!!!!!!----------loc :  " + location.getLatitude() + "  ---  " + location.getLongitude());
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 50000, 0, locationListener);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
