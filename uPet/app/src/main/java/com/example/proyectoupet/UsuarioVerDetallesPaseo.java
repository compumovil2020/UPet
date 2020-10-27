package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoupet.services.CustomSpinnerMascotasAdapter;
import com.example.proyectoupet.services.MapsServices.MapService;
import com.example.proyectoupet.services.permissionService.PermissionService;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsuarioVerDetallesPaseo extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback {
    private static final int MAP_PERMISSION = 11;
    private static final int REQUEST_CHECK_SETTINGS = 3;

    private PermissionService permissionService;

    private GoogleMap mMap;
    private Marker mMarkerPosActual;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    Geocoder mGeocoder;
    LatLng UA;
    boolean actualizarRuta = false;
    private int tipoRuta;



    private String[] nombresPerros={"Becquer","Elias","Lune","Paco","Taika"};
    private int perros[] = {R.drawable.perrito, R.drawable.perro_elias, R.drawable.perro_lune, R.drawable.pug, R.drawable.perfil};

    private MapService mapService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_ver_detalles_paseo);
        mapService = new MapService(mMap);
        permissionService = new PermissionService();
        TextView txtNombrePaseador = findViewById(R.id.txtNombrePaseadorDetallesPaseo);
        txtNombrePaseador.setText("Ricardo Arjona");
        TextView txtFecha = findViewById(R.id.txtFechaDetallesPaseo);
        txtFecha.setText("11 de Noviembre");
        TextView txtHora = findViewById(R.id.txtHoraDetallesPaseo);
        txtHora.setText("8:00AM");
        TextView txtNumMascotas = findViewById(R.id.txtNumeroMascotasDetallesPaseo);
        txtNumMascotas.setText("5 Mascotas");
        TextView txtRanking = findViewById(R.id.txtRankingDetallesPaseo);
        txtRanking.setText("Excelente");
        TextView txtCosto = findViewById(R.id.txtCostoDetallesPaseo);
        txtCosto.setText("10000$");
        Intent intent = getIntent();
        tipoRuta = Integer.parseInt(intent.getStringExtra("TIPO_RUTA"));
        Button btnRealizarSeguimiento = findViewById(R.id.btnRealizarSeguimientoDetallesPaseo);
        btnRealizarSeguimiento.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getBaseContext(),UsuarioSeguimientoPaseadorActivity.class);
                String rutaNueva = tipoRuta+"";
                intent.putExtra("TIPO_RUTA",rutaNueva);
                startActivity(intent);
            }
        });
        Button btnAnterior = findViewById(R.id.btnAnteriorDetallesPaseo);
        btnAnterior.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                actualizarRuta = false;
                if(tipoRuta == 5)
                {
                    tipoRuta = 2;
                }
                else if(tipoRuta == 2){
                    tipoRuta = 3;
                }
                else if(tipoRuta == 3)
                {
                    tipoRuta = 5;
                }
            }
        });
        Button btnSiguiente = findViewById(R.id.btnSiguienteDetallesPaseo);
        btnSiguiente.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                actualizarRuta = false;
                if(tipoRuta == 2)
                {
                    tipoRuta = 5;
                }
                else if(tipoRuta == 3){
                    tipoRuta = 2;
                }
                else if(tipoRuta == 5)
                {
                    tipoRuta = 3;
                }
            }
        });
        Button btnVolver = findViewById(R.id.btnVolverDetallesPaseo);
        btnVolver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

        String[] nombrePerros = obtenerNombresPerros();
        int perros[] = obtenerPerros();
        Spinner spinnerMascotas = findViewById(R.id.spinnerUDetallesPaseo);
        spinnerMascotas.setOnItemSelectedListener(this);
        CustomSpinnerMascotasAdapter customAdapter = new CustomSpinnerMascotasAdapter(getApplicationContext(),perros,nombresPerros);
        spinnerMascotas.setAdapter(customAdapter);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapUDetallesPaseo);
        mapFragment.getMapAsync(this);
        mGeocoder = new Geocoder(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    //mMap.clear();
                    LatLng ubicacionactual = new LatLng(location.getLatitude(),location.getLongitude());
                    if(mMarkerPosActual != null)
                    {
                        mMarkerPosActual.remove();
                    }
                    mMarkerPosActual = mapService.addMarker(ubicacionactual,"Ubicacion Actual");
                    if(!ubicacionactual.equals(UA))
                    {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacionactual));
                    }
                    UA = ubicacionactual;
                    if(!actualizarRuta) {
                        mMap.clear();
                        mMarkerPosActual = mapService.addMarker(ubicacionactual,"Ubicacion Actual");
                        List<LatLng> puntosRuta = obtenerRutas(tipoRuta);
                        for (LatLng puntoRuta: puntosRuta) {
                            mapService.addMarker(puntoRuta, geoCoderSearch(puntoRuta));
                        }
                        mapService.makeRoute(UsuarioVerDetallesPaseo.this, puntosRuta , "CLOSEST");
                        actualizarRuta = true;
                    }
                }
            }
        };
        String[] permisos = {Manifest.permission.ACCESS_FINE_LOCATION};
        mLocationRequest = createLocationRequest();
        permissionService.requestPermission(this, permisos);
        checkSettings();

    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); //tasa de refresco en milisegundos
        locationRequest.setFastestInterval(5000); //máxima tasa de refresco
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    private void checkSettings(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //currentLocation();
                startLocationUpdates(); //Todas las condiciones para recibir localizaciones
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
                            resolvable.startResolutionForResult(UsuarioVerDetallesPaseo.this,REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                        } break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Map<String, Boolean> responses = permissionService.managePermissionResponse(this,requestCode,permissions,grantResults);
        if(responses.get(Manifest.permission.ACCESS_FINE_LOCATION))
        {
            checkSettings();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                if (resultCode == RESULT_OK) {
                    //currentLocation();
                    startLocationUpdates(); //Se encendió la localización!!!
                } else {
                    Toast.makeText(this, "Sin acceso a localización, hardware deshabilitado!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }
    private void stopLocationUpdates(){
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mapService = new MapService(mMap);
    }
    private String geoCoderSearch(LatLng latLng){
        String address = "";
        try{
            List<Address> results = mGeocoder.getFromLocation(latLng.latitude,latLng.longitude,2);
            if(results != null && results.size()>0){
                address = results.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }


    public List<LatLng> obtenerRutas(int i)
    {
        List<LatLng> ruta = new ArrayList<LatLng>();
        ruta.add(new LatLng(4.675336, -74.058865));
        ruta.add(new LatLng(4.682190, -74.060082));
        if(i > 3)
        {
            ruta.add(new LatLng(4.680294, -74.057813));
        }
        if(i > 4)
        {
            ruta.add(new LatLng(4.673896, -74.044640));
        }
        if(i > 5)
        {
            ruta.add(new LatLng(4.681056, -74.046577));
        }
        return ruta;
    }
    public String[] obtenerNombresPerros()
    {
        String[] nombresPerros = new String[3];
        int random;
        random = (int)(Math.random() * 5);
        nombresPerros[0] = this.nombresPerros[random];
        random = (int)(Math.random() * 5);
        nombresPerros[1] = this.nombresPerros[random];
        random = (int)(Math.random() * 5);
        nombresPerros[2] = this.nombresPerros[random];

        return nombresPerros;
    }

    public int[] obtenerPerros()
    {
        int[] perros = new int[3];
        int random;
        random = (int)(Math.random() * 5);
        perros[0] = this.perros[random];
        random = (int)(Math.random() * 5);
        perros[1] = this.perros[random];
        random = (int)(Math.random() * 5);
        perros[2] = this.perros[random];

        return perros;
    }
}