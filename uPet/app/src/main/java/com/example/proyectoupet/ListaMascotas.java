package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.proyectoupet.model.Mascota;
import com.example.proyectoupet.services.CustomListViewMascotasAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaMascotas extends AppCompatActivity {

    Button editar_crear;
    List<String> arreglo;
    ListView listaMascotas;
    List<String> idsMascotas;
    List<Mascota> mascotas;
    List<Bitmap> imagenMascotas;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private StorageReference mStorageRef;
    private CustomListViewMascotasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mascotas);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        listaMascotas = findViewById(R.id.listaMascotas);
        initListaMascota();
        listaMascotas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getBaseContext(), PerfilMascota.class).putExtra("mascota",mascotas.get(position)).
                        putExtra("mascotaId",idsMascotas.get(position)).putExtra("crear",false));
            }
        });
    }

    public void toCrearMascota(View v){
        startActivity(new Intent(getBaseContext(), CrearEditarMascota.class).putExtra("crear",true));
    }

    private void initListaMascota(){
        firebaseFirestore.collection("mascotas").whereEqualTo("userId",firebaseAuth.getUid()).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        mascotas = new ArrayList<>();
                        idsMascotas = new ArrayList<>();
                        arreglo = new ArrayList<>();
                        imagenMascotas = new ArrayList<>();
                        for(DocumentSnapshot document : value.getDocuments()){
                            idsMascotas.add(document.getId());
                            Mascota m = document.toObject(Mascota.class);
                            mascotas.add(m);
                            arreglo.add(m.getNombreMascota());
                        }
                        for(int i = 0; i<arreglo.size();i++)
                        {
                            imagenMascotas.add(null);
                        }
                        int i = 0;
                        for(String idMascota: idsMascotas)
                        {
                            try {
                                downloadFile(idMascota,i);
                            } catch (IOException e) {

                            }
                            i++;
                        }
                        adapter = new CustomListViewMascotasAdapter(ListaMascotas.this,idsMascotas,arreglo,imagenMascotas);

                        listaMascotas = (ListView) findViewById(R.id.listaMascotas);
                        listaMascotas.setAdapter(adapter);
                    }
                });
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