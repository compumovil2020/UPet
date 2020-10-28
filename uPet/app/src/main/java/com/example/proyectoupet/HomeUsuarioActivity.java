package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class HomeUsuarioActivity extends AppCompatActivity {

    private ImageButton btnPerfil;
    private ImageButton btnAdminPaseos;
    private ImageButton btnSitiosInteres;
    private ImageButton btnAdminMascotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_usuario);
        btnPerfil =findViewById(R.id.btnPerfilUsuario);
        btnAdminPaseos = findViewById(R.id.btnAdminPaseosUsuario);
        btnSitiosInteres = findViewById(R.id.btnSitiosInteresUsuario);
        btnAdminMascotas = findViewById(R.id.btnAdminMascotasUsuario);

        btnPerfil.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(getBaseContext(), VerPerfil.class));
            }
        });

        btnSitiosInteres.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(getBaseContext(), LugaresdeInteres.class));
            }
        });

        btnAdminPaseos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(getBaseContext(),UsuarioAdministrarPaseosActivity.class));
            }
        });

        btnAdminMascotas.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(getBaseContext(), ListaMascotas.class));
            }
        });


    }
}