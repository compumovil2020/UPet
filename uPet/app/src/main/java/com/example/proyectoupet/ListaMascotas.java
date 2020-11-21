package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.proyectoupet.model.Mascota;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mascotas);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
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
                        for(DocumentSnapshot document : value.getDocuments()){
                            idsMascotas.add(document.getId());
                            Mascota m = document.toObject(Mascota.class);
                            mascotas.add(m);
                            arreglo.add(m.getNombreMascota());
                        }
                        arreglo = mascotas.stream().map(x -> x.getNombreMascota()).collect(Collectors.toList());
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListaMascotas.this,
                                R.layout.item_list_fuente, arreglo);
                        listaMascotas = (ListView) findViewById(R.id.listaMascotas);
                        listaMascotas.setAdapter(adapter);
                    }
                });
    }

}