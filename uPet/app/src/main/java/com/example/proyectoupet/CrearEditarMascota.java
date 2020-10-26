package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

public class CrearEditarMascota extends AppCompatActivity {

    EditText nombreMascota;
    EditText edad;
    EditText especie;
    EditText raza;
    Button crear;
    ImageButton seleccion_imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_editar_mascota);

        nombreMascota = findViewById(R.id.editTextNombreMascota);
        edad = findViewById(R.id.editTextEdadMascota);
        especie = findViewById(R.id.editTextEspecie);
        raza = findViewById(R.id.editTextRaza);
        crear = findViewById(R.id.buttonCrearMascota);
        seleccion_imagen =  findViewById(R.id.boton_seleccion_imagen);

        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        seleccion_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(CrearEditarMascota.this, seleccion_imagen);
                popup.getMenuInflater().inflate(R.menu.menu_foto, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(CrearEditarMascota.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                popup.show();
            }
        });

    }

}