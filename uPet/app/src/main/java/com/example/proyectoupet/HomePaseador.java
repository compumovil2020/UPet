package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.proyectoupet.paseos.CrearPaseo;

public class HomePaseador extends AppCompatActivity {

    Button botAgendarPaseo;
    Button botSitiosInteres;
    Button botPerfil;
    Button botAdminMascota;
    Button botAdminPaseo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_paseador);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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