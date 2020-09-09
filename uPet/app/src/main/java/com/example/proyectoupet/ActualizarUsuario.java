package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ActualizarUsuario extends AppCompatActivity {

    EditText nombreUsuario;
    EditText nombres;
    EditText apellidos;
    EditText email;
    EditText celular;
    EditText direccion;
    EditText ciudad;
    EditText barrio;
    Button actualizarInfo;
    Button volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_usuario);

        nombreUsuario = findViewById(R.id.editTextNombreUsuario);
        nombres = findViewById(R.id.editTextNombres);
        apellidos = findViewById(R.id.editTextApellidos);
        email = findViewById(R.id.editTextEmail);
        celular = findViewById(R.id.editTextCelular);
        direccion = findViewById(R.id.editTextDireccion);
        ciudad = findViewById(R.id.editTextCiudad);
        barrio = findViewById(R.id.editTextBarrio);
        actualizarInfo = findViewById(R.id.act_botonActualizar);
        volver = findViewById(R.id.act_botonVolver);

        actualizarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), VerPerfil.class));
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), VerPerfil.class));
            }
        });

    }
}