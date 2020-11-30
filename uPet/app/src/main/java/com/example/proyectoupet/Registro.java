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

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

public class Registro extends AppCompatActivity implements Validator.ValidationListener{


    Button botCancelar;
    Button botSiguiente;
    @NotEmpty(message = "campo requerido")
    @Email(message = "Ingrese un email valido")
    EditText email;
    @NotEmpty(message = "campo requerido")
    @Length(min = 6,message = "Debe tener seis caracteres minimo")
    EditText pass;
    @NotEmpty(message = "campo requerido")
    EditText nombre;
    @NotEmpty(message = "campo requerido")
    EditText apellido;
    @NotEmpty(message = "campo requerido")
    EditText usuario;
    Spinner spin;


    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        pass = findViewById(R.id.registro_contra);
        email = findViewById(R.id.registro_email);
        nombre = findViewById(R.id.registro_nombres);
        apellido = findViewById(R.id.registro_apellidos);
        usuario = findViewById(R.id.registro_usuario);
        spin = findViewById(R.id.spinner2);


        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    public void validateForNextSignUpStep(View v){
        validator.validate();
    }

    public void toLogIn(View v){
        startActivity(new Intent(this, InicioSesion.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public void onValidationSucceeded() {
        Intent i = new Intent(this,RegistroExt.class);
        i.putExtra("nombreUsuario",usuario.getText().toString());
        i.putExtra("nombre",nombre.getText().toString());
        i.putExtra("apellido",apellido.getText().toString());
        i.putExtra("tipoUsuario",String.valueOf(spin.getSelectedItem()));
        i.putExtra("email",email.getText().toString());
        i.putExtra("contrasena", pass.getText().toString());
        startActivity(i);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            // Display error messages
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

}