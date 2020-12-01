package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.proyectoupet.model.EstadoPaseo;
import com.example.proyectoupet.model.Mascota;
import com.example.proyectoupet.model.MascotaPuntoRecogida;
import com.example.proyectoupet.model.Parada;
import com.example.proyectoupet.model.PaseoSolicitar;
import com.example.proyectoupet.services.CustomListviewMascotasCheckboxAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class UsuarioSeleccionarMascotasActivity extends AppCompatActivity {


    private String idPaseo;
    private LatLng latLng;
    private String ubitext;

    private CustomListviewMascotasCheckboxAdapter adapter;
    private ListView listaPerros;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;

    private List<String> idMascotas;
    private List<Mascota> mascotas;
    private List<String> nombresMascotas;
    private List<Bitmap> imagenMascotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_seleccionar_mascotas);
        Intent intent = getIntent();
        idPaseo = intent.getExtras().getString("idPaseo");
        latLng = (LatLng) intent.getExtras().get("latlong");
        ubitext = intent.getExtras().getString("ubitext");
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Button btnContinuar = findViewById(R.id.btnContinuarSM);
        btnContinuar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String punto = "¡Paseo Solicitado!";
                List<String>idAux = new ArrayList<>();
                for(int i = 0;  i < mascotas.size();i++)
                {
                    idAux.add(idMascotas.get(i));
                }
                Parada nuevaParada = new Parada(latLng.latitude,latLng.longitude,ubitext);
                MascotaPuntoRecogida nueva = new MascotaPuntoRecogida(idAux,firebaseAuth.getUid(),nuevaParada, EstadoPaseo.SOLICITADO.toString());
                db.collection("paseosSolicitados").whereEqualTo("idPaseo",idPaseo).get().addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot ds: queryDocumentSnapshots.getDocuments())
                                {
                                    PaseoSolicitar pso = ds.toObject(PaseoSolicitar.class);
                                    pso.getMascotasPuntoRecogida().add(nueva);
                                    db.collection("paseosSolicitados").document(ds.getId()).set(pso);
                                }
                            }
                        }
                );
                Toast.makeText(getBaseContext(),punto, Toast.LENGTH_LONG);
                Intent intent = new Intent(getBaseContext(), UsuarioAdministrarPaseosActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button btnVolver = findViewById(R.id.btnVolverSM);
        btnVolver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

        listaPerros = findViewById(R.id.listaUMascotas);


        listaPerros.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String o = (String) parent.getItemAtPosition(position);
                Toast.makeText(getBaseContext(),o,Toast.LENGTH_SHORT);
            }
        });
        initMascotas();

    }

    public void initMascotas()
    {
        db.collection("mascotas").whereEqualTo("userId",firebaseAuth.getUid()).addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        mascotas = new ArrayList<>();
                        idMascotas = new ArrayList<>();
                        nombresMascotas = new ArrayList<>();
                        imagenMascotas = new ArrayList<>();
                        for(DocumentSnapshot document : value.getDocuments()){
                            idMascotas.add(document.getId());
                            Mascota m = document.toObject(Mascota.class);
                            mascotas.add(m);
                            nombresMascotas.add(m.getNombreMascota());
                            for(int i = 0; i<nombresMascotas.size();i++)
                            {
                                imagenMascotas.add(null);
                            }
                            adapter = new CustomListviewMascotasCheckboxAdapter(getApplicationContext(),idMascotas,nombresMascotas,imagenMascotas);
                            listaPerros.setAdapter(adapter);
                            int i = 0;
                            for(String idMascota: idMascotas)
                            {
                                try {
                                    downloadFile(idMascota,i);
                                } catch (IOException e) {

                                }
                                i++;
                            }
                        }
                    }
                }
        );
    }

    private void downloadFile(String id, int pos) throws IOException {

        File localFile = File.createTempFile(id, "jpg");
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

}