package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyectoupet.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import lombok.NonNull;

public class RegistroExt extends AppCompatActivity implements Validator.ValidationListener{
    public static final String TAG = "ProyectoUpet";

    @NotEmpty(message = "Campo requerido")
    @Length(min = 10,max = 10,message = "numero invalido")
    EditText celularEditText;
    @NotEmpty(message = "Campo requerido")
    EditText direccionEditText;
    @NotEmpty(message = "Campo requerido")
    EditText ciudadEditText;
    @NotEmpty(message = "Campo requerido")
    EditText barrioEditText;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_ext);

        celularEditText = findViewById(R.id.registro_celular);
        direccionEditText = findViewById(R.id.registro_dir);
        ciudadEditText = findViewById(R.id.registro_ciudad);
        barrioEditText = findViewById(R.id.registro_barrio);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    private void updateUI(FirebaseUser user){
        String spinItem = getIntent().getStringExtra("tipoUsuario");
        if(user != null){
            if(spinItem.equals("Normal")==true){
                startActivity(new Intent(this, HomeUsuarioActivity.class));
            }else{
                startActivity(new Intent(this, HomePaseador.class));
            }
        }
    }

    public void backToRegistro(View v){
        finish();
    }

    public void makeRegistration(View v){
        this.validator.validate();
    }

    private void signUpUser(){
        String email = getIntent().getStringExtra("email");
        String pass = getIntent().getStringExtra("contrasena");
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            persistUserData(user);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistroExt.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void persistUserData(FirebaseUser firebaseUser){
        UserData userData = getUserData();
        firebaseFirestore.collection("usuarios").document(firebaseUser.getUid()).set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("!!!Usuario", "DocumentSnapshot successfully written!");
                updateUI(firebaseUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Log.w("!!!Usuario", "Error adding document", e);
            }
        });
    }

    private UserData getUserData(){
        String nombre = getIntent().getStringExtra("nombre");
        String apellido = getIntent().getStringExtra("apellido");
        String usuario = getIntent().getStringExtra("nombreUsuario");
        String tipoUsuario = getIntent().getStringExtra("tipoUsuario");
        String celular = celularEditText.getText().toString();
        String direccion = direccionEditText.getText().toString();
        String ciudad = ciudadEditText.getText().toString();
        String barrio = barrioEditText.getText().toString();
        return new UserData(nombre,apellido,usuario,tipoUsuario,celular,direccion,ciudad,barrio);
    }

    @Override
    public void onValidationSucceeded() {
        signUpUser();
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