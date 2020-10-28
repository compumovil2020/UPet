package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Registro extends AppCompatActivity {

    Button botCancelar;
    Button botSiguiente;
    EditText email;
    EditText pass;
    EditText nombre;
    EditText apellido;
    EditText usuario;
    Spinner spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        botCancelar = findViewById(R.id.boton_cancelar);
        botSiguiente = findViewById(R.id.boton_siguiente);
        pass = findViewById(R.id.registro_contra);
        email = findViewById(R.id.registro_email);
        nombre = findViewById(R.id.registro_nombres);
        apellido = findViewById(R.id.registro_apellidos);
        usuario = findViewById(R.id.registro_usuario);
        spin = findViewById(R.id.spinner2);

        botCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), InicioSesion.class));
            }
        });

        botSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()==true){
                    if(isEmailValid(email.getText().toString())==true){
                        Intent intent = new Intent(getBaseContext(), RegistroExt.class);
                        intent.putExtra("email", email.getText().toString());
                        intent.putExtra("pass",pass.getText().toString());
                        intent.putExtra("itemSpin",spin.getSelectedItem().toString());
                        startActivity(intent);
                    }else{
                        Toast.makeText(Registro.this, "Ingrese un correo válido por favor",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        String password = pass.getText().toString();
        if (TextUtils.isEmpty(password)) {
            pass.setError("Required.");
            valid = false;
        } else {
            pass.setError(null);
        }
        String correo = email.getText().toString();
        if (TextUtils.isEmpty(correo)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }
        String nom = nombre.getText().toString();
        if (TextUtils.isEmpty(nom)) {
            nombre.setError("Required.");
            valid = false;
        } else {
            nombre.setError(null);
        }
        String ap = apellido.getText().toString();
        if (TextUtils.isEmpty(ap)) {
            apellido.setError("Required.");
            valid = false;
        } else {
            apellido.setError(null);
        }
        String user = usuario.getText().toString();
        if (TextUtils.isEmpty(user)) {
            usuario.setError("Required.");
            valid = false;
        } else {
            usuario.setError(null);
        }
        return valid;
    }

    private boolean isEmailValid(String email) {
        if (!email.contains("@") ||
                !email.contains(".") ||
                email.length() < 5)
            return false;
        return true;
    }
}