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

        botAgendarPaseo = findViewById(R.id.boton_agPaseo);
        botSitiosInteres = findViewById(R.id.boton_sitInteres);
        botPerfil = findViewById(R.id.boton_perfil);
        botAdminMascota = findViewById(R.id.boton_adminMascota);
        botAdminPaseo = findViewById(R.id.boton_adminPaseo);

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
    }

    public void toCrearPaseo(View v){
        startActivity(new Intent(this, CrearPaseo.class));
    }
}