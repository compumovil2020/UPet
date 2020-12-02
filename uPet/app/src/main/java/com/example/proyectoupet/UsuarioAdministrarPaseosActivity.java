package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import com.example.proyectoupet.model.Paseo;
import com.example.proyectoupet.model.PaseoUsuario;
import com.example.proyectoupet.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsuarioAdministrarPaseosActivity extends AppCompatActivity {

    public List<String> paseos;
    public List<String> idPaseosAgendados;

    private Button btnVerDetalles;
    private Button btnBuscarPaseo;
    private Button btnVolver;
    private ListView listaPaseos;
    private String paseoSeleccionado;
    private int positionPaseoSeleccionado;
    private Toolbar toolbar;

    private ArrayAdapter<String> adapter;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_administrar_paseos);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        btnVerDetalles = findViewById(R.id.btnVerDetallesAP);
        btnBuscarPaseo = findViewById(R.id.btnBuscarPaseoAP);
        btnVolver = findViewById(R.id.btnVolverAP);
        btnVerDetalles.setEnabled(false);
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        listaPaseos = findViewById(R.id.listaUPaseos);
        listaPaseos.setChoiceMode(listaPaseos.CHOICE_MODE_SINGLE);
        setSupportActionBar(toolbar);
        initPaseosUsuario();
        listaPaseos.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btnVerDetalles.setEnabled(true);
                paseoSeleccionado = (String) listaPaseos.getItemAtPosition(position);
                positionPaseoSeleccionado = position;
            }
        });

        btnVerDetalles.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getBaseContext(),UsuarioVerDetallesPaseo.class);
                intent.putExtra("idPaseo",idPaseosAgendados.get(positionPaseoSeleccionado));
                startActivity(intent);
            }
        });

        btnBuscarPaseo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getBaseContext(),PaseosDisponibles.class);
                startActivity(intent);
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                db.collection("usuarios").document(firebaseAuth.getUid()).
                        get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            UserData userData = task.getResult().toObject(UserData.class);
                            if(userData.getTipoUsuario().equals("Paseador")){
                                startActivity(new Intent(UsuarioAdministrarPaseosActivity.this,HomePaseador.class));
                            }else{
                                startActivity(new Intent(UsuarioAdministrarPaseosActivity.this,HomeUsuarioActivity.class));
                            }
                        } else {
                            Log.w("Error", "Error getting documents.", task.getException());
                        }
                    }
                });
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menupaseo,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.enviar_mensaje){
            Intent intent = new Intent(UsuarioAdministrarPaseosActivity.this, UserChat.class);
            startActivity(intent);
        }
        return true;
    }

    public void initPaseosUsuario()
    {
        db.collection("paseosUsuario").whereEqualTo("idUsuario",firebaseAuth.getUid()).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        paseos = new ArrayList<>();
                        idPaseosAgendados = new ArrayList<>();
                        for(DocumentSnapshot document : value.getDocuments()){
                            PaseoUsuario p = document.toObject(PaseoUsuario.class);
                            for(String idPaseo: p.getPaseosAgendados())
                            {
                                idPaseosAgendados.add(idPaseo);
                                db.collection("paseosAgendados").document(idPaseo).get().addOnSuccessListener(
                                        new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Paseo paseo = documentSnapshot.toObject(Paseo.class);
                                                paseos.add("Paseo "+paseo.getFecha()+" "+paseo.getHoraInicio()+"-"+paseo.getHoraFin());
                                                if(adapter != null)
                                                {
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        }
                                );

                            }
                        }
                        adapter = new ArrayAdapter<String>(UsuarioAdministrarPaseosActivity.this,android.R.layout.simple_list_item_1,paseos);
                        listaPaseos.setAdapter(adapter);
                    }
                });
    }

    public List<String> obtenerPaseos()
    {
        List<String> paseos = new ArrayList<String>();
        paseos.add("Paseo Miercoles 11 Noviembre 8:00AM - Ricardo Arjona");
        paseos.add("Paseo Miercoles 18 Noviembre 8:00AM - Ricardo Arjona");
        paseos.add("Paseo Miercoles 25 Noviembre 8:00AM - Ricardo Arjona");
        return paseos;
    }
}