package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.proyectoupet.paseos.CrearPaseo;
import com.example.proyectoupet.services.background.CambioSolicitudPaseos;
import com.example.proyectoupet.services.background.UserLocationTracker;
import com.google.firebase.auth.FirebaseAuth;

public class HomePaseador extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DrawerLayout sideBar;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_paseador);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
            Intent intent = new Intent(HomePaseador.this, InicioSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    public void toCrearPaseo(View v){
        startActivity(new Intent(this, CrearPaseo.class));
    }

    public void toPaseosAgendados(View v){
        startActivity(new Intent(getBaseContext(), PaseosAgendadosActivity.class));
    }

    public void toPaseosSolicitados(View v){
        startActivity(new Intent(getBaseContext(), UsuarioAdministrarPaseosActivity.class));
    }

    public void toListarMascotas(View v){
        startActivity(new Intent(getBaseContext(), ListaMascotas.class));
        //SI ESPERO UN RESULTADO DE LA OTRA ACTIVIDAD, COLOCAR START ACTIVITY FOR RESULT
    }

    public void toPerfil(View v){
        startActivity(new Intent(getBaseContext(), VerPerfil.class));
    }

    public void toSitiosInteres(View v){
        startActivity(new Intent(getBaseContext(), LugaresdeInteres.class));
    }
}