package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyectoupet.model.UserData;
import com.example.proyectoupet.services.background.CambioSolicitudPaseos;
import com.example.proyectoupet.services.background.UserLocationTracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

public class InicioSesion extends AppCompatActivity implements Validator.ValidationListener {
    public static final String TAG = "ProyectoUpet";


    Button botLogin, botRegistro;

    @NotEmpty(message = "Campo requerido")
    @Email(message = "Email invalido")
    EditText emailEditText;
    @NotEmpty(message = "Campo requerido")
    @Length(min = 6, message = "Contrasena muy corta")
    EditText passwordEditText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        botRegistro = findViewById(R.id.boton_siguiente);
        botLogin = findViewById(R.id.boton_login);
        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        validator = new Validator(this);
        validator.setValidationListener(this);

        botLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignin();
            }
        });

        botRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), Registro.class));
            }
        });


    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    private void updateUI(FirebaseUser user){
        if(user != null){
            firebaseFirestore.collection("usuarios").document(user.getUid()).
                    get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        UserData userData = task.getResult().toObject(UserData.class);
                        if(userData.getTipoUsuario().equals("Paseador")){
                            startActivity(new Intent(InicioSesion.this,HomePaseador.class));
                        }else{
                            startActivity(new Intent(InicioSesion.this,HomeUsuarioActivity.class));
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });
        }else{
            passwordEditText.setText("");
            //limpiar el formulario
        }
    }

    private void attemptSignin(){
        this.validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        String email = this.emailEditText.getText().toString();
        String pass = passwordEditText.getText().toString();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(InicioSesion.this, "Por favor verifique la información",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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