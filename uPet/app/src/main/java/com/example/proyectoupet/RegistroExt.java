package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import lombok.NonNull;

public class RegistroExt extends AppCompatActivity {
    public static final String TAG = "ProyectoUpet";
    Button botRegistro;
    Button botAtras;
    EditText celular;
    EditText direccion;
    EditText ciudad;
    EditText barrio;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_ext);

        botRegistro = findViewById(R.id.inf_boton_registro);
        botAtras = findViewById(R.id.inf_boton_atras);
        celular = findViewById(R.id.registro_celular);
        direccion = findViewById(R.id.registro_dir);
        ciudad = findViewById(R.id.registro_ciudad);
        barrio = findViewById(R.id.registro_barrio);
        mAuth = FirebaseAuth.getInstance();


        botRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()==true){
                    signUpUser();
                }
            }
        });

        botAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), Registro.class));
            }
        });
    }

    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    private void updateUI(FirebaseUser user){
        String spinItem = getIntent().getStringExtra("itemSpin");
        if(user != null){
            if(spinItem.equals("Normal")==true){
                startActivity(new Intent(this, HomeUsuarioActivity.class));
            }else{
                startActivity(new Intent(this, HomePaseador.class));
            }
        }else{

        }
    }

    private void signUpUser(){
        String email = getIntent().getStringExtra("email");
        String pass = getIntent().getStringExtra("pass");
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistroExt.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;
        String cel = celular.getText().toString();
        if (TextUtils.isEmpty(cel)) {
            celular.setError("Required.");
            valid = false;
        } else {
            celular.setError(null);
        }
        String dir = direccion.getText().toString();
        if (TextUtils.isEmpty(dir)) {
            direccion.setError("Required.");
            valid = false;
        } else {
            direccion.setError(null);
        }
        String city = ciudad.getText().toString();
        if (TextUtils.isEmpty(city)) {
            ciudad.setError("Required.");
            valid = false;
        } else {
            ciudad.setError(null);
        }
        String barr = barrio.getText().toString();
        if (TextUtils.isEmpty(barr)) {
            barrio.setError("Required.");
            valid = false;
        } else {
            barrio.setError(null);
        }
        return valid;
    }
}