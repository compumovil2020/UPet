package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proyectoupet.model.UserData;
import com.example.proyectoupet.services.ImageService;
import com.example.proyectoupet.services.ImageServiceFunction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.IOException;

public class VerPerfil extends AppCompatActivity {

    public static final String IMAGE_ROUTE = "fotos_perfil/";

    Button actualizarInfo;
    Button volver;
    TextView usuarioPerfil;
    TextView nombrePerfil;
    TextView emailPerfil;
    TextView telefonoPerfil;
    TextView direccionPerfil;
    TextView ciudadPerfil;
    TextView barrioPerfil;
    ImageView imagenPerfil;
    Uri imagen_perfil;

    private FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_perfil);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        usuarioPerfil = findViewById(R.id.textView16);
        nombrePerfil= findViewById(R.id.textView17);
        emailPerfil = findViewById(R.id.textView18);
        telefonoPerfil = findViewById(R.id.textView19);
        direccionPerfil = findViewById(R.id.textView24);
        ciudadPerfil = findViewById(R.id.textView25);
        barrioPerfil = findViewById(R.id.textView27);
        imagenPerfil = findViewById(R.id.imageView4);

        actualizarInfo = findViewById(R.id.ver_botonActualizar);
        volver = findViewById(R.id.ver_BotonVolver);

        initData();

        actualizarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), ActualizarUsuario.class));
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onRestart(){
        super.onRestart();
        initData();
        cargarImagen(firebaseAuth.getUid());
    }

    public void initData(){
        firebaseFirestore.collection("usuarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(firebaseAuth.getUid())){
                                    UserData us = document.toObject(UserData.class);
                                    usuarioPerfil.setText(us.getUsuario());
                                    String nomcom = us.getNombre() + " " + us.getApellido();
                                    nombrePerfil.setText(nomcom);
                                    emailPerfil.setText(firebaseAuth.getCurrentUser().getEmail());
                                    telefonoPerfil.setText(us.getCelular());
                                    direccionPerfil.setText(us.getDireccion());
                                    ciudadPerfil.setText(us.getCiudad());
                                    barrioPerfil.setText(us.getBarrio());
                                    cargarImagen(firebaseAuth.getUid());
                                }
                                Log.i("jaja", document.getId() + " => " + document.toObject(UserData.class));
                            }
                        } else {
                            Log.i("jaja", "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    private void cargarImagen(String key){
        try {
            Log.i("jaja","entro a imagen");
            File localFile = File.createTempFile(key, "jpg");
            File finalLocalFile = localFile;
            ImageServiceFunction function = (params) -> {
                Log.i("jaja","entro a funcionnnnnnnnnn");
                imagenPerfil.setImageIcon(Icon.createWithFilePath(finalLocalFile.getPath()));
            };
            ImageService.downloadImage(IMAGE_ROUTE+key,localFile,function);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}