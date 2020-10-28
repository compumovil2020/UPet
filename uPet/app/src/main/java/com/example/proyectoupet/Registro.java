package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Registro extends AppCompatActivity {

    Button botCancelar;
    Button botSiguiente;
    EditText email;
    EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        botCancelar = findViewById(R.id.boton_cancelar);
        botSiguiente = findViewById(R.id.boton_siguiente);
        pass = findViewById(R.id.registro_contra);
        email = findViewById(R.id.registro_email);

        botCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), InicioSesion.class));
            }
        });

        botSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(getBaseContext(), RegistroExt.class);
               intent.putExtra("email", email.getText().toString());
               intent.putExtra("pass",pass.getText().toString());
               startActivity(intent);
            }
        });
    }
}