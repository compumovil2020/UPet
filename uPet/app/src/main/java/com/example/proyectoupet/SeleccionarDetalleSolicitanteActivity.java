package com.example.proyectoupet;



import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.example.proyectoupet.paseos.SeleccionRutaPaseo;
import com.example.proyectoupet.services.MapsServices.MapService;
import com.example.proyectoupet.services.permissionService.PermissionService;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SeleccionarDetalleSolicitanteActivity extends FragmentActivity implements OnMapReadyCallback
{

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private MapService mapService;
    private GoogleMap mMap;
    private PermissionService permissionService = new PermissionService();
    private List<LatLng> walkStops;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_detalle_solicitante);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            this.permissionService.requestPermission(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        }else {
            start();
        }
    }

    private void start(){
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(SeleccionarDetalleSolicitanteActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionService.managePermissionResponse(this,requestCode,permissions,grantResults);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            start();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapService = new MapService(mMap);
        walkStops = new ArrayList<>();
        walkStops.add(new LatLng(4.711930995014736, -74.22606725245716));
        walkStops.add(new LatLng(4.714638216287915, -74.22221392393112));
        walkStops.add(new LatLng(4.710979355113475, -74.2234155535698));
        walkStops.add(new LatLng(4.70588665611004, -74.22185853123665));
        walkStops.add(new LatLng(4.706317035894434, -74.2253990471363));
        walkStops.add(new LatLng(4.708188917543524, -74.22723267227411));
        walkStops.add(new LatLng(4.710117264471713, -74.2250020802021));
        LatLng randomPoint = walkStops.get(new Random().nextInt(walkStops.size()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(randomPoint));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        mapService.makeRoute(SeleccionarDetalleSolicitanteActivity.this, walkStops, MapService.Order.FIFO);
        mapService.addMarker(randomPoint, mapService.getLatLngName(this, randomPoint));
    }

    public void openConfirmarSolicitante(View v)
    {
        finish();
    }

    public void openCancelarSolicitante(View v)
    {
        finish();
    }

    public void volverDetalle(View v){
        finish();
    }

}
