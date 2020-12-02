package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyectoupet.model.EstadoPaseo;
import com.example.proyectoupet.model.Mascota;
import com.example.proyectoupet.model.MascotaPuntoRecogida;
import com.example.proyectoupet.model.Parada;
import com.example.proyectoupet.model.Paseo;
import com.example.proyectoupet.model.PaseoSolicitar;
import com.example.proyectoupet.model.UserData;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsuarioSeguimientoPaseadorActivity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener, OnMapReadyCallback {
    private static final int MAP_PERMISSION = 11;
    private static final int REQUEST_CHECK_SETTINGS = 3;

    private PermissionService permissionService;

    private List<String> idMascotas;
    private List<String> nombreMascotas;
    private List<Mascota> mascotas;
    private List<Bitmap> imagenMascotas;
    private List<LatLng> puntosRuta;
    private LatLng ubicacionPaseadorPrim;
    private LatLng ubicacionPaseadorSeg;

    private GoogleMap mMap;
    private Marker mMarkerPosActual;
    private Marker mMarkerPosPaseador;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    Geocoder mGeocoder;
    LatLng UA;
    boolean actualizarRuta = false;
    private String idPaseo;
    private String idPaseador;

    private Spinner spinnerMascotas;

    private CustomSpinnerMascotasAdapter adapter;

    private MapService mapService;

    private FirebaseFirestore db;

    private FirebaseAuth firebaseAuth;

    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_seguimiento_paseador);
        Intent intent = getIntent();
        idPaseo = intent.getExtras().getString("idPaseo");
        idPaseador = intent.getExtras().getString("idPaseador");
        puntosRuta = new ArrayList<>();
        permissionService = new PermissionService();
        spinnerMascotas = findViewById(R.id.spinnerSeguimientoPaseador);
        spinnerMascotas.setOnItemSelectedListener(this);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapSeguimientoPaseador);
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
                    if(mMarkerPosPaseador != null)
                    {
                        mMarkerPosActual.remove();
                    }
                    mMarkerPosActual = mapService.addMarker(ubicacionactual,"Ubicacion Actual");
                    if(!ubicacionPaseadorPrim.equals(ubicacionPaseadorSeg))
                    {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacionPaseadorPrim));
                    }
                    ubicacionPaseadorSeg = ubicacionPaseadorPrim;
                    UA = ubicacionactual;
                    if(!actualizarRuta) {
                        mMap.clear();
                        mMarkerPosActual = mapService.addMarker(ubicacionactual,"Ubicacion Actual");
                        for (LatLng puntoRuta: puntosRuta) {
                            mapService.addMarker(puntoRuta, geoCoderSearch(puntoRuta));
                        }
                        mapService.makeRoute(UsuarioSeguimientoPaseadorActivity.this, puntosRuta , MapService.Order.CLOSEST);
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
                            resolvable.startResolutionForResult(UsuarioSeguimientoPaseadorActivity.this,REQUEST_CHECK_SETTINGS);
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
        initPaseo();
        initRuta();
        initMascotas();
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

    public void initRuta()
    {
        db.collection("paseosAgendados").document(idPaseo).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Paseo paseo = task.getResult().toObject(Paseo.class);
                        for(Parada parada: paseo.getParadas())
                        {
                            puntosRuta.add(new LatLng(parada.getLatitude(), parada.getLongitude()));
                        }
                        actualizarRuta = false;

                    }
                }
        );
    }

    public void initMascotas()
    {
        db.collection("paseosSolicitados").whereEqualTo("idPaseo",idPaseo).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        idMascotas = new ArrayList<>();
                        nombreMascotas = new ArrayList<>();
                        imagenMascotas = new ArrayList<>();
                        mascotas = new ArrayList<>();
                        for(DocumentSnapshot document : value.getDocuments()){
                            PaseoSolicitar pr = document.toObject(PaseoSolicitar.class);
                            pr.getMascotasPuntoRecogida();
                            for(MascotaPuntoRecogida mpr:pr.getMascotasPuntoRecogida())
                            {
                                if(mpr.getUsuarioId().equals(firebaseAuth.getUid()) && mpr.getEstado().equals(EstadoPaseo.CONFIRMADO.toString()))
                                {
                                    idMascotas = mpr.getMascotasId();
                                    for(String idMascota: idMascotas)
                                    {
                                        db.collection("mascotas").document(idMascota).get().addOnCompleteListener(
                                                new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        Mascota mascota = task.getResult().toObject(Mascota.class);
                                                        mascotas.add(mascota);
                                                        nombreMascotas.add(mascota.getNombreMascota());
                                                        for(int i = 0; i<nombreMascotas.size();i++)
                                                        {
                                                            imagenMascotas.add(null);
                                                        }
                                                        int i = 0;
                                                        for(String idMascota: idMascotas)
                                                        {
                                                            try {
                                                                downloadFileMascotas(idMascota,i);
                                                            } catch (IOException e) {

                                                            }
                                                            i++;
                                                        }
                                                        spinnerMascotas.setOnItemSelectedListener(UsuarioSeguimientoPaseadorActivity.this);
                                                        adapter = new CustomSpinnerMascotasAdapter(getApplicationContext(),idMascotas,nombreMascotas,imagenMascotas);
                                                        spinnerMascotas.setAdapter(adapter);
                                                    }
                                                }
                                        );
                                    }


                                }
                            }

                        }

                    }
                });
    }

    private void downloadFileMascotas(String id, int pos) throws IOException {

        File localFile = File.createTempFile("images", "jpg");
        StorageReference imageRef = mStorageRef.child("fotos_mascotas/"+id);
        imageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        try {
                            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(localFile));
                            imagenMascotas.set(pos,b);
                            adapter.notifyDataSetChanged();
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
        imageRef.getFile(localFile)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception)
                    {
                        imagenMascotas.add(pos,null);
                    }
                });
    }

    public void initPaseo()
    {
        db.collection("ubicacion").document(idPaseador).addSnapshotListener(
                new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Parada parada = value.toObject(Parada.class);
                        LatLng lat = new LatLng(parada.getLatitude(),parada.getLongitude());
                        ubicacionPaseadorPrim = lat;
                        mMarkerPosPaseador = mapService.addMarker(lat,"Paseador");
                        actualizarRuta = false;
                    }
                }
        );
    }

}