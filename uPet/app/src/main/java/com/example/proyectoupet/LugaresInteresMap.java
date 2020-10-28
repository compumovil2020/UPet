package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.example.proyectoupet.model.PlaceOfInteresType;
import com.example.proyectoupet.paseos.SeleccionRutaPaseo;
import com.example.proyectoupet.services.MapsServices.MapService;
import com.example.proyectoupet.services.permissionService.PermissionService;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LugaresInteresMap  extends FragmentActivity implements OnMapReadyCallback  {

    public static final int REQUEST_CHECK_SETTINGS = 136;
    private GoogleMap mMap;
    private Location currentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private MapService mapService;
    private PermissionService permissionService = new PermissionService();
    private List<Marker> markerList;
    Map<PlaceOfInteresType,Boolean> types;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugares_interes_map);
        types = new HashMap<>();
        Intent i = getIntent();
        types.put(PlaceOfInteresType.PARK,i.getExtras().getBoolean(PlaceOfInteresType.PARK.name()));
        types.put(PlaceOfInteresType.PETSTORE,i.getExtras().getBoolean(PlaceOfInteresType.PETSTORE.name()));
        types.put(PlaceOfInteresType.VETERINAY,i.getExtras().getBoolean(PlaceOfInteresType.VETERINAY.name()));

        markerList = new ArrayList<>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            this.permissionService.requestPermission(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        }else {

            Task<Location> task = mFusedLocationClient.getLastLocation();
            task.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    start(location);
                }
            });
            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(LugaresInteresMap.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sendEx) {
                            } break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });
        }
    }

    public void start(Location location){
        if(location != null){
            currentLocation = location;
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(LugaresInteresMap.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionService.managePermissionResponse(this,requestCode,permissions,grantResults);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            Task<Location> task = mFusedLocationClient.getLastLocation();
            task.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    start(location);
                }
            });
            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(LugaresInteresMap.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sendEx) {
                            } break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapService = new MapService(mMap);
        LatLng userPosition = new LatLng(this.currentLocation.getLatitude(), this.currentLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userPosition));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        List<String> queryTypes = new ArrayList<>();
        List<String> hexColors = new ArrayList<>();
        if(types.get(PlaceOfInteresType.PARK)){
            queryTypes.add("park");
            hexColors.add("#2bb555");
        }
        if(types.get(PlaceOfInteresType.PETSTORE)){
            queryTypes.add("pet_store");
            hexColors.add("#1abec9");
        }
        if(types.get(PlaceOfInteresType.VETERINAY)){
            queryTypes.add("veterinary_care");
            hexColors.add("#8d0cc9");
        }
        mapService.findPlaces(LugaresInteresMap.this,userPosition, queryTypes,markerList,hexColors);
    }
}