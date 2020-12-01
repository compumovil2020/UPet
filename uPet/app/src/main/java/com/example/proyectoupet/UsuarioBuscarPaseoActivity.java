package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoupet.model.Parada;
import com.example.proyectoupet.model.Paseo;
import com.example.proyectoupet.model.UserData;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsuarioBuscarPaseoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MAP_PERMISSION = 11;
    private static final int REQUEST_CHECK_SETTINGS = 3;

    private List<Paseo> paseosList;
    private List<String> idPaseos;
    private String idPaseo;
    private List<UserData> paseadores;
    private String fecha;
    private int paseoActualPos;

    private List<LatLng> puntosRuta;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    private PermissionService permissionService;

    private GoogleMap mMap;
    private Marker mMarkerPosActual;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    Geocoder mGeocoder;
    private MapService mapService;
    LatLng UA;
    boolean actualizarRuta = false;


    private TextView txtNombrePaseador;

    private TextView txtFecha;

    private TextView txtHora;

    private TextView txtNumMascotas;

    private TextView txtRanking;

    private TextView txtCosto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_buscar_paseo);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        permissionService = new PermissionService();
        txtNombrePaseador = findViewById(R.id.txtNombrePaseadorBuscarPaseo);
        txtFecha = findViewById(R.id.txtFechaBuscarPaseo);
        txtHora = findViewById(R.id.txtHoraBuscarPaseo);
        txtNumMascotas = findViewById(R.id.txtNumeroMascotasBuscarPaseo);
        txtRanking = findViewById(R.id.txtRankingBuscarPaseo);
        txtCosto = findViewById(R.id.txtCostoBuscarPaseo);
        puntosRuta = new ArrayList<>();
        Intent intent = getIntent();
        idPaseo = intent.getExtras().getString("idPaseo");
        fecha  = intent.getExtras().getString("fecha");
        initPaseo();
        Button btnSeleccionar = findViewById(R.id.btnSeleccionarBP);
        btnSeleccionar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(getBaseContext(), UsuarioSeleccionarPuntoActivity.class);
                i.putExtra("idPaseo",idPaseos.get(paseoActualPos));
                startActivity(i);
            }
        });
        Button btnSiguiente = findViewById(R.id.btnSiguienteBP);
        btnSiguiente.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(paseosList.size() != 1)
                {
                    actualizarRuta = false;
                    if(paseoActualPos == paseosList.size()-1)
                    {
                        paseoActualPos = 0;
                    }
                    else{
                        paseoActualPos++;
                    }
                    setPaseoActual(paseoActualPos);
                }

            }
        });
        Button btnVolver = findViewById(R.id.btnVolverBP);
        btnVolver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapUBuscarPaseo);
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
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacionactual));
                        List<LatLng> puntosRuta  = getPuntosRuta();
                        for (LatLng puntoRuta: puntosRuta) {
                            mapService.addMarker(puntoRuta, geoCoderSearch(puntoRuta));
                        }
                        mapService.makeRoute(UsuarioBuscarPaseoActivity.this, puntosRuta , MapService.Order.FIFO);
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

    public void initPaseo()
    {
        if(fecha.equals("No"))
        {
            db.collection("paseosAgendados").
                    addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            paseosList = new ArrayList<>();
                            idPaseos = new ArrayList<>();
                            paseadores = new ArrayList<>();
                            puntosRuta = new ArrayList<>();
                            int posSet = 0;
                            for(DocumentSnapshot document : value.getDocuments()){
                                idPaseos.add(document.getId());
                                Paseo p = document.toObject(Paseo.class);
                                paseosList.add(p);
                                db.collection("usuarios").document(p.getUserId()).get().addOnSuccessListener(
                                        new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                paseadores.add(documentSnapshot.toObject(UserData.class));
                                                int posSet = 0;
                                                if(document.getId().equals(idPaseo))
                                                {
                                                    posSet = idPaseos.size()-1;
                                                    setPaseoActual(posSet);
                                                }
                                            }
                                        }
                                );
                            }

                        }
                    });
        }
        else {
            db.collection("paseosAgendados")
                    .whereEqualTo("fecha",fecha).
                    addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            paseosList = new ArrayList<>();
                            idPaseos = new ArrayList<>();
                            paseadores = new ArrayList<>();
                            puntosRuta = new ArrayList<>();
                            for(DocumentSnapshot document : value.getDocuments()){
                                idPaseos.add(document.getId());
                                Paseo p = document.toObject(Paseo.class);
                                paseosList.add(p);
                                db.collection("usuarios").document(p.getUserId()).get().addOnSuccessListener(
                                        new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                paseadores.add(documentSnapshot.toObject(UserData.class));
                                                int posSet = 0;
                                                if(document.getId().equals(idPaseo))
                                                {
                                                    posSet = idPaseos.size()-1;
                                                    setPaseoActual(posSet);
                                                }
                                            }
                                        }
                                );
                            }


                        }
                    });
        }

    }

    public void setPaseoActual(int posicion)
    {
        UserData paseador = paseadores.get(posicion);
        Paseo paseoActual = paseosList.get(posicion);
        String nombre = paseador.getNombre() + " " + paseador.getApellido();
        String precio = "$"+paseoActual.getPrecio();
        String hora = paseoActual.getHoraInicio() + "-" + paseoActual.getHoraFin();
        txtNombrePaseador.setText(nombre);
        txtCosto.setText(precio);
        txtFecha.setText(paseoActual.getFecha());
        txtHora.setText(hora);
        txtRanking.setText("Excelente");
        txtNumMascotas.setText(paseoActual.getCapacidad()+"");
        obtenerRutas(paseoActual);
        actualizarRuta = false;
        this.paseoActualPos = posicion;
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
                            resolvable.startResolutionForResult(UsuarioBuscarPaseoActivity.this,REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                        } break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Map<String, Boolean> responses = permissionService.managePermissionResponse(this,requestCode,permissions,grantResults);
        if(responses.get(Manifest.permission.ACCESS_FINE_LOCATION))
        {
            checkSettings();
        }
    }

    public List<LatLng> getPuntosRuta()
    {
        return this.puntosRuta;
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


    public void obtenerRutas(Paseo paseo)
    {
        List<LatLng> ruta = new ArrayList<LatLng>();
        for(Parada parada: paseo.getParadas())
        {
            LatLng punto = new LatLng(parada.getLatitude(),parada.getLongitude());
            ruta.add(punto);
        }
        puntosRuta = ruta;
    }
}