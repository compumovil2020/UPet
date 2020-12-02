package com.example.proyectoupet;



import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.example.proyectoupet.model.EstadoPaseo;
import com.example.proyectoupet.model.MascotaPuntoRecogida;
import com.example.proyectoupet.model.Parada;
import com.example.proyectoupet.model.Paseo;
import com.example.proyectoupet.model.PaseoSolicitar;
import com.example.proyectoupet.model.PaseoUsuario;
import com.example.proyectoupet.model.UserData;
import com.example.proyectoupet.paseos.SeleccionRutaPaseo;
import com.example.proyectoupet.services.ImageService;
import com.example.proyectoupet.services.ImageServiceFunction;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SeleccionarDetalleSolicitanteActivity extends FragmentActivity implements OnMapReadyCallback
{

    private List<String> solicitantes;

    private List<MascotaPuntoRecogida> listaMascotas;

    private List<String> idPaseosSolicitantes;

    private TextView textoNombreSolicitante;

    private TextView puntoRecogidaSolicitante;

    private FirebaseFirestore db;

    private FirebaseAuth firebaseAuth;

    private String idUsuario;

    private Parada parada;

    private String idPaseo;

    private ImageView imagenSolicitante;

    private TextView numeroSolicitante;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private MapService mapService;
    private GoogleMap mMap;
    private PermissionService permissionService = new PermissionService();
    private List<LatLng> walkStops;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_detalle_solicitante);
        textoNombreSolicitante = findViewById(R.id.de_textView_1);
        puntoRecogidaSolicitante = findViewById(R.id.de_textView_2);
        imagenSolicitante = findViewById(R.id.imagenUsuarioDetalleSolicitante);
        numeroSolicitante = findViewById(R.id.de_textView_Numero);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        idUsuario = getIntent().getExtras().getString("idUsuario");
        parada = (Parada) getIntent().getExtras().get("parada");
        idPaseo = getIntent().getExtras().getString("idPaseo");
        db.collection("usuarios").document(idUsuario).get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserData ud = documentSnapshot.toObject(UserData.class);
                        textoNombreSolicitante.setText(ud.getNombre() +" "+ ud.getApellido());
                        cargarImagenPaseador(idUsuario);
                    }
                }
        );
        puntoRecogidaSolicitante.setText(parada.getTitle());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            this.permissionService.requestPermission(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        }else {
            start();
        }
    }

    private void start(){
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapSeleccionarDetalleSolicitante);
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
        db.collection("paseosAgendados").document(idPaseo).get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        walkStops = new ArrayList<>();
                        Paseo p = documentSnapshot.toObject(Paseo.class);
                        for(Parada parada: p.getParadas())
                        {
                            walkStops.add(new  LatLng(parada.getLatitude(),parada.getLongitude()));
                        }
                        mapService.makeRoute(SeleccionarDetalleSolicitanteActivity.this, walkStops, MapService.Order.FIFO);
                    }
                }
        );

        LatLng puntoRecogida = new LatLng(parada.getLatitude(),parada.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(puntoRecogida));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        mapService.addMarker(puntoRecogida, mapService.getLatLngName(this, puntoRecogida));
    }

    public void openConfirmarSolicitante(View v)
    {
        db.collection("paseosSolicitados").whereEqualTo("idPaseo",idPaseo).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            PaseoSolicitar p = null;
                            String idPaseoSolicitar = "";
                            for( QueryDocumentSnapshot dsPaseoSolicitado : task.getResult()) {
                                p = dsPaseoSolicitado.toObject(PaseoSolicitar.class);
                                idPaseoSolicitar = dsPaseoSolicitado.getId();
                                if (p != null)
                                {
                                    List<MascotaPuntoRecogida> nueva = new ArrayList<>();
                                    MascotaPuntoRecogida agregar = null;
                                    for(MascotaPuntoRecogida mpr2: p.getMascotasPuntoRecogida())
                                    {
                                        if(mpr2.getUsuarioId().equals(idUsuario))
                                        {
                                            mpr2.setEstado(EstadoPaseo.CONFIRMADO.toString());
                                            nueva.add(mpr2);
                                            db.collection("paseosUsuario").whereEqualTo("idUsuario",mpr2.getUsuarioId()).get().addOnSuccessListener(
                                                    new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            if(!queryDocumentSnapshots.getDocuments().isEmpty())
                                                            {
                                                                PaseoUsuario pu = queryDocumentSnapshots.getDocuments().get(0).toObject(PaseoUsuario.class);
                                                                pu.getPaseosAgendados().add(idPaseo);
                                                                db.collection("paseosUsuario").document(queryDocumentSnapshots.getDocuments().get(0).getId()).update("paseosAgendados",pu.getPaseosAgendados());
                                                            }
                                                            else{
                                                                List<String> paseosA = new ArrayList<>();
                                                                List<String> paseosC = new ArrayList<>();
                                                                paseosA.add(idPaseo);
                                                                PaseoUsuario pu = new PaseoUsuario(idUsuario,paseosA,paseosC);
                                                                db.collection("paseosUsuario").add(pu);
                                                            }

                                                        }
                                                    }
                                            ).addOnFailureListener(
                                                    new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    }
                                            );

                                        }
                                        else
                                        {
                                            nueva.add(mpr2);
                                        }
                                    }
                                    p.setMascotasPuntoRecogida(nueva);
                                    db.collection("paseosSolicitados").document(idPaseoSolicitar).update("mascotasPuntoRecogida",nueva);



                                }
                            }

                        }

                    }
                });

        finish();
    }

    public void openCancelarSolicitante(View v)
    {
        db.collection("paseosSolicitados").whereEqualTo("idPaseo",idPaseo).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            PaseoSolicitar p = null;
                            String idPaseoSolicitar = "";
                            for( QueryDocumentSnapshot dsPaseoSolicitado : task.getResult()) {
                                p = dsPaseoSolicitado.toObject(PaseoSolicitar.class);
                                idPaseoSolicitar = dsPaseoSolicitado.getId();
                                if (p != null)
                                {
                                    List<MascotaPuntoRecogida> nueva = new ArrayList<>();
                                    for(MascotaPuntoRecogida mpr2: p.getMascotasPuntoRecogida())
                                    {
                                        if(mpr2.getUsuarioId().equals(idUsuario))
                                        {
                                            mpr2.setEstado(EstadoPaseo.CANCELADO.toString());
                                            db.collection("paseosUsuario").whereEqualTo("idUsuario",mpr2.getUsuarioId()).get().addOnSuccessListener(
                                                    new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            if(!queryDocumentSnapshots.getDocuments().isEmpty())
                                                            {
                                                                PaseoUsuario pu = queryDocumentSnapshots.getDocuments().get(0).toObject(PaseoUsuario.class);
                                                                pu.getPaseosCancelados().add(idPaseo);
                                                                db.collection("paseosUsuario").document(queryDocumentSnapshots.getDocuments().get(0).getId()).update("paseosAgendados",pu.getPaseosCancelados());
                                                            }
                                                            else{
                                                                List<String> paseosA = new ArrayList<>();
                                                                List<String> paseosC = new ArrayList<>();
                                                                paseosC.add(idPaseo);
                                                                PaseoUsuario pu = new PaseoUsuario(idUsuario,paseosA,paseosC);
                                                                db.collection("paseosUsuario").add(pu);
                                                            }

                                                        }
                                                    }
                                            ).addOnFailureListener(
                                                    new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    }
                                            );

                                        }
                                        nueva.add(mpr2);
                                    }
                                    p.setMascotasPuntoRecogida(nueva);
                                    db.collection("paseosSolicitados").document(idPaseoSolicitar).update("mascotasPuntoRecogida",nueva);
                                }
                            }

                        }

                    }
                });

        finish();
    }

    public void detalleMascotasRecoger(View v){
        startActivity(new Intent(getBaseContext(), ListarMascotasSolicitarActivity.class).putExtra("idUsuario",idUsuario).putExtra("idPaseo", idPaseo));
    }

    private void cargarImagenPaseador(String key){
        try {
            File localFile = File.createTempFile(key, "jpg");
            ImageServiceFunction function = (params) -> {
                imagenSolicitante.setImageIcon(Icon.createWithFilePath(localFile.getPath()));
            };
            ImageService.downloadImage("fotos_perfil"+key,localFile,function);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void volverDetalle(View v){
        finish();
    }

}
