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
import android.graphics.drawable.Icon;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoupet.model.EstadoPaseo;
import com.example.proyectoupet.model.Mascota;
import com.example.proyectoupet.model.MascotaPuntoRecogida;
import com.example.proyectoupet.model.Parada;
import com.example.proyectoupet.model.Paseo;
import com.example.proyectoupet.model.PaseoSolicitar;
import com.example.proyectoupet.model.PaseoUsuario;
import com.example.proyectoupet.model.UserData;
import com.example.proyectoupet.services.CustomSpinnerMascotasAdapter;
import com.example.proyectoupet.services.ImageService;
import com.example.proyectoupet.services.ImageServiceFunction;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private String idPaseo;
    private String idActual;
    private String paseadorActual;
    private Paseo paseoActual;

    private TextView txtNombrePaseador;

    private TextView txtFecha;

    private TextView txtHora;

    private TextView txtNumMascotas;

    private TextView txtRanking;

    private TextView txtCosto;

    private Spinner spinnerMascotas;

    private CustomSpinnerMascotasAdapter adapter;

    private ImageView imagenPaseador;


    public List<String> paseos;
    private List<Paseo> paseosList;
    private List<String> idPaseos;
    private List<String> idPaseadores;
    private List<UserData> paseadores;
    private int paseoActualPos;
    private List<LatLng> puntosRuta;

    private List<String> idMascotas;
    private List<String> nombreMascotas;
    private List<Mascota> mascotas;
    private List<Bitmap> imagenMascotas;


    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;


    private MapService mapService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_ver_detalles_paseo);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mapService = new MapService(mMap);
        permissionService = new PermissionService();
        txtNombrePaseador = findViewById(R.id.txtNombrePaseadorDetallesPaseo);
        txtFecha = findViewById(R.id.txtFechaDetallesPaseo);
        txtHora = findViewById(R.id.txtHoraDetallesPaseo);
        txtNumMascotas = findViewById(R.id.txtNumeroMascotasDetallesPaseo);
        txtRanking = findViewById(R.id.txtRankingDetallesPaseo);
        txtRanking.setText("Excelente");
        txtCosto = findViewById(R.id.txtCostoDetallesPaseo);
        imagenPaseador = findViewById(R.id.imagenPaseadorDetallesPaseo);
        Intent intent = getIntent();
        idPaseo = intent.getExtras().getString("idPaseo");

        Button btnRealizarSeguimiento = findViewById(R.id.btnRealizarSeguimientoDetallesPaseo);
        btnRealizarSeguimiento.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Calendar rightNow = Calendar.getInstance();
                DateFormat dateFormatF = new SimpleDateFormat("dd/MM/yyyy");
                DateFormat dateFormatH = new SimpleDateFormat("HH:mm");
                Date date = new Date();
                String dateformatted = dateFormatH.format(date);
                String fechaActual = dateFormatF.format(date);

                System.out.println(dateformatted);
                if(!paseosList.isEmpty())
                {
                    Paseo paseo = paseoActual;
                    Intent intent = new Intent(getBaseContext(),UsuarioSeguimientoPaseadorActivity.class);
                    intent.putExtra("idPaseo",idActual);
                    if(!idPaseadores.isEmpty())
                    {
                        intent.putExtra("idPaseador",paseadorActual);
                        startActivity(intent);
                    }

                }


            }
        });
        Button btnAnterior = findViewById(R.id.btnAnteriorDetallesPaseo);
        btnAnterior.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                actualizarRuta = false;
                if(paseoActualPos == paseosList.size()-1)
                {
                    paseoActualPos = 0;
                }
                else{
                    paseoActualPos++;
                }
                //setPaseoActual(paseoActualPos);
            }
        });
        Button btnSiguiente = findViewById(R.id.btnSiguienteDetallesPaseo);
        btnSiguiente.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if(paseosList.size() != 1)
                {
                    actualizarRuta = false;
                    if(paseoActualPos == 0)
                    {
                        paseoActualPos = paseosList.size()-1;
                    }
                    else{
                        paseoActualPos--;
                    }
                    //setPaseoActual(paseoActualPos);
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

        spinnerMascotas = findViewById(R.id.spinnerUDetallesPaseo);

        initPaseo();
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
                    UA = ubicacionactual;
                    if(!actualizarRuta) {
                        mMap.clear();
                        mMarkerPosActual = mapService.addMarker(ubicacionactual,"Ubicacion Actual");
                        List<LatLng> puntosRuta = getPuntosRuta();
                        int i = 0;
                        for (LatLng puntoRuta: puntosRuta) {
                            if(i == 0)
                            {
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(puntoRuta));
                                i++;
                            }
                            mapService.addMarker(puntoRuta, geoCoderSearch(puntoRuta));
                        }
                        mapService.makeRoute(UsuarioVerDetallesPaseo.this, puntosRuta , MapService.Order.CLOSEST);
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

    public void initPaseo()
    {

        db.collection("paseosUsuario").whereEqualTo("idUsuario",firebaseAuth.getUid()).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        paseosList = new ArrayList<>();
                        idPaseos = new ArrayList<>();
                        paseadores = new ArrayList<>();
                        puntosRuta = new ArrayList<>();
                        idPaseadores = new ArrayList<>();
                        for(DocumentSnapshot document : value.getDocuments()){
                            PaseoUsuario p = document.toObject(PaseoUsuario.class);
                            for(String idP: p.getPaseosAgendados())
                            {
                                idPaseos.add(idP);
                                db.collection("paseosAgendados").document(idP).get().addOnCompleteListener(
                                        new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                Paseo paseo = task.getResult().toObject(Paseo.class);
                                                db.collection("usuarios").document(paseo.getUserId()).get().addOnCompleteListener(
                                                        new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                paseadores.add(task.getResult().toObject(UserData.class));
                                                                idPaseadores.add(paseo.getUserId());
                                                                paseosList.add(paseo);
                                                                int posSet = 0;
                                                                if(idP.equals(idPaseo))
                                                                {
                                                                    posSet = idPaseos.size()-1;
                                                                    idActual = idP;
                                                                    paseadorActual = paseo.getUserId();
                                                                    paseoActual = paseo;
                                                                    setPaseoActual(task.getResult().toObject(UserData.class),paseo);
                                                                }
                                                            }
                                                        }
                                                );

                                            }
                                        }
                                );


                            }
                        }


                    }
                });

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
        actualizarRuta = false;
    }

    public List<LatLng> getPuntosRuta()
    {
        return this.puntosRuta;
    }


    public void setPaseoActual(UserData paseadorV, Paseo paseoV)
    {
        UserData paseador = paseadorV;
        Paseo paseoActual = paseoV;
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
        initMascotasPaseo();
        actualizarRuta = false;
    }

    public void initMascotasPaseo()
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
                                                        spinnerMascotas.setOnItemSelectedListener(UsuarioVerDetallesPaseo.this);
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

    private void cargarImagenPaseador(String key){
        try {
            File localFile = File.createTempFile(key, "jpg");
            ImageServiceFunction function = (params) -> {
                imagenPaseador.setImageIcon(Icon.createWithFilePath(localFile.getPath()));
            };
            ImageService.downloadImage("fotos_perfil"+key,localFile,function);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}