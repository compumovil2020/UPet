package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PerfilMascota extends AppCompatActivity {

    TextView nombreMascota;
    TextView edad;
    TextView especie;
    TextView raza;
    Button botoneditar;
    Button botonborrar;
    ImageView imagen_mascota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_mascota);

        nombreMascota = findViewById(R.id.showTextNombreMascota);
        edad = findViewById(R.id.showTextEdadMascota);
        especie = findViewById(R.id.showTextEspecie);
        raza = findViewById(R.id.showTextRaza);
        botoneditar = findViewById(R.id.botonEditarMascota);
        botonborrar =  findViewById(R.id.botonBorrarMascota);
        imagen_mascota = findViewById(R.id.showImagenMascota);

        botoneditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), CrearEditarMascota.class));
            }
        });

        botonborrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Borrado", Toast.LENGTH_SHORT).show();
            }
        });
    }
}