package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class HomePaseador extends AppCompatActivity {

    Button botAgendarPaseo;
    Button botSitiosInteres;
    Button botPerfil;
    Button botAdminMascota;
    Button botAdminPaseo;
    Button botLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_paseador);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        botAgendarPaseo = findViewById(R.id.boton_agPaseo);
        botSitiosInteres = findViewById(R.id.boton_sitInteres);
        botPerfil = findViewById(R.id.boton_perfil);
        botAdminMascota = findViewById(R.id.boton_adminMascota);
        botAdminPaseo = findViewById(R.id.boton_adminPaseo);
        botLogout = findViewById(R.id.boton_logout);

        botAgendarPaseo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), AgendarPaseo.class));
            }
        });

        botSitiosInteres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), LugaresdeInteres.class));
            }
        });

        botPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), VerPerfil.class));
            }
        });

        botAdminMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), ListaMascotas.class));
                //SI ESPERO UN RESULTADO DE LA OTRA ACTIVIDAD, COLOCAR START ACTIVITY FOR RESULT
            }
        });

        botAdminPaseo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), PaseosAgendadosActivity.class));
            }
        });

        botLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(HomePaseador.this, InicioSesion.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}