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

import com.example.proyectoupet.services.background.CambioSolicitudPaseos;
import com.example.proyectoupet.services.background.UserLocationTracker;
import com.google.firebase.auth.FirebaseAuth;

public class HomeUsuarioActivity extends AppCompatActivity {

    private ImageButton btnPerfil;
    private ImageButton btnAdminPaseos;
    private ImageButton btnSitiosInteres;
    private ImageButton btnAdminMascotas;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_usuario);
        mAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        startService(new Intent(getApplicationContext(), UserLocationTracker.class));
        startService(new Intent(getApplicationContext(), CambioSolicitudPaseos.class));
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
            mAuth.signOut();
            Intent intent = new Intent(HomeUsuarioActivity.this, InicioSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}