package com.example.proyectoupet.paseos;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.proyectoupet.R;
import com.example.proyectoupet.services.MapsServices.MapService;
import com.example.proyectoupet.services.permissionService.PermissionService;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SeleccionRutaPaseo extends FragmentActivity implements OnMapReadyCallback{

    public static final int REQUEST_CHECK_SETTINGS = 136;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private MapService mapService;
    private PermissionService permissionService = new PermissionService();
    private Location currentLocation;
    private List<Marker> checkpoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_ruta_paseo);
        this.checkpoints = new ArrayList<>();
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
                                resolvable.startResolutionForResult(SeleccionRutaPaseo.this, REQUEST_CHECK_SETTINGS);
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
                                resolvable.startResolutionForResult(SeleccionRutaPaseo.this, REQUEST_CHECK_SETTINGS);
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
            supportMapFragment.getMapAsync(SeleccionRutaPaseo.this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapService = new MapService(mMap);
        LatLng userPosition = new LatLng(this.currentLocation.getLatitude(), this.currentLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userPosition));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Marker m = mapService.addMarker(latLng,mapService.getLatLngName(SeleccionRutaPaseo.this,latLng));
                m.setDraggable(true);
                checkpoints.add(m);
                if(checkpoints.size() > 1){
                    Marker start = checkpoints.get(checkpoints.size()-2);
                    Marker end = checkpoints.get(checkpoints.size() -1);
                    mapService.makeRoute(SeleccionRutaPaseo.this,start.getPosition(),end.getPosition(),(checkpoints.size()-2)+"-"+(checkpoints.size() -1));
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SeleccionRutaPaseo.this);
                builder.setCancelable(true);
                builder.setTitle("Borrar parada?");
                builder.setMessage("Quiere borrar esta parada de ");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int index = checkpoints.indexOf(m);
                                if(index > 0 ){
                                    mapService.removePolylineFromMap((index - 1)+"-"+index);
                                }
                                if(index < checkpoints.size() -1){
                                    mapService.removePolylineFromMap(index+"-"+(index+1));
                                }
                                m.remove();
                                checkpoints.remove(index);
                                List<LatLng> points = checkpoints.stream().map(x->x.getPosition()).collect(Collectors.toList());
                                mapService.makeRoute(SeleccionRutaPaseo.this,points, MapService.Order.FIFO);
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });



        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            private int index;
            @Override
            public void onMarkerDragStart(Marker marker) {
                index = checkpoints.indexOf(marker);
                if(index > 0){
                    mapService.removePolylineFromMap((index-1)+"-"+index);
                }
                if(index < checkpoints.size() - 1){
                    mapService.removePolylineFromMap(index+"-"+(index+1));
                }
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if(index > 0){
                    mapService.makeRoute(SeleccionRutaPaseo.this,checkpoints.get(index-1).getPosition(),marker.getPosition(),(index-1)+"-"+index);
                }
                if(index < checkpoints.size() - 1){
                    mapService.makeRoute(SeleccionRutaPaseo.this,marker.getPosition(),checkpoints.get(index+1).getPosition(),index+"-"+(index+1));
                }
            }
        });
    }

}