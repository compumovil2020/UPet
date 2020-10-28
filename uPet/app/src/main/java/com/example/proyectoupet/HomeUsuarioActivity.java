package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class HomeUsuarioActivity extends AppCompatActivity {

    private ImageButton btnPerfil;
    private ImageButton btnAdminPaseos;
    private ImageButton btnSitiosInteres;
    private ImageButton btnAdminMascotas;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_usuario);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnPerfil =findViewById(R.id.btnPerfilUsuario);
        btnAdminPaseos = findViewById(R.id.btnAdminPaseosUsuario);
        btnSitiosInteres = findViewById(R.id.btnSitiosInteresUsuario);
        btnAdminMascotas = findViewById(R.id.btnAdminMascotasUsuario);

        btnPerfil.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

            }
        });

        btnSitiosInteres.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

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

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logout){
            System.out.println("salio");
            ///aqui agregas tu logica
        }
        return true;
    }
}