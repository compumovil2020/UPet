package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.proyectoupet.model.Mascota;
import com.example.proyectoupet.model.MascotaPuntoRecogida;
import com.example.proyectoupet.model.PaseoSolicitar;
import com.example.proyectoupet.services.CustomListViewMascotasAdapter;
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
import java.util.stream.Collectors;

public class ListarMascotasSolicitarActivity extends AppCompatActivity {

    private ListView listaMascotas;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference mStorageRef;

    private CustomListViewMascotasAdapter adapter;

    private String idPaseo;
    private String idUsuario;
    private List<String> idMascotas;
    private List<String> nombreMascotas;
    private List<Mascota> mascotas;
    private List<Bitmap> imagenMascotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_mascotas_solicitar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        listaMascotas = findViewById(R.id.listaMascotasSolicitar);
        idMascotas = new ArrayList<>();
        nombreMascotas = new ArrayList<>();
        imagenMascotas = new ArrayList<>();
        idPaseo = getIntent().getExtras().getString("idPaseo");
        idUsuario = getIntent().getExtras().getString("idUsuario");
        listaMascotas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getBaseContext(), VerDetalleMascotaActivity.class).putExtra("mascota",mascotas.get(position)).
                        putExtra("mascotaId",idMascotas.get(position)));
            }
        });
        initMascotas();


    }
    public void initMascotas()
    {
        firebaseFirestore.collection("paseosSolicitados").whereEqualTo("idPaseo",idPaseo).addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        mascotas = new ArrayList<>();
                        idMascotas = new ArrayList<>();
                        nombreMascotas = new ArrayList<>();
                        imagenMascotas = new ArrayList<>();
                        for(DocumentSnapshot document : value.getDocuments()){
                            PaseoSolicitar ps = document.toObject(PaseoSolicitar.class);
                            for(MascotaPuntoRecogida mpr : ps.getMascotasPuntoRecogida())
                            {
                                if(mpr.getUsuarioId().equals(idUsuario))
                                {
                                    idMascotas = mpr.getMascotasId();
                                }
                            }
                        }
                        for(String idMascota: idMascotas)
                        {
                            firebaseFirestore.collection("mascotas").document(idMascota).get().addOnSuccessListener(
                                    new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Mascota mascota = documentSnapshot.toObject(Mascota.class);
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
                                                    downloadFile(idMascota,i);
                                                } catch (IOException e) {

                                                }
                                                i++;
                                            }
                                            adapter = new CustomListViewMascotasAdapter(ListarMascotasSolicitarActivity.this,idMascotas,nombreMascotas,imagenMascotas);

                                            listaMascotas = (ListView) findViewById(R.id.listaMascotasSolicitar);
                                            listaMascotas.setAdapter(adapter);
                                        }
                                    }
                            );
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