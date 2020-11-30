package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.proyectoupet.model.UserData;
import com.example.proyectoupet.services.ImageService;
import com.example.proyectoupet.services.ImageServiceFunction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ActualizarUsuario extends AppCompatActivity {


    final int REQUEST_IMAGE_CAPTURE=1;
    final int IMAGE_PICKER_REQUEST=100;
    public static final String IMAGE_ROUTE = "fotos_perfil/";
    EditText nombreUsuario;
    EditText nombres;
    EditText apellidos;
    EditText email;
    EditText celular;
    EditText direccion;
    EditText ciudad;
    EditText barrio;
    String tipous;
    Button actualizarInfo;
    Button volver;
    ImageView imgPerfil;
    ImageView imgEdit;

    Uri img_perfil;

    private FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    boolean imgbandera = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_usuario);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

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
        imgPerfil = findViewById(R.id.imageViewPerfil);
        imgEdit = findViewById(R.id.imageView5);

        cargarImagen(firebaseAuth.getUid());
        initData();


        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ActualizarUsuario.this, imgEdit);
                popup.getMenuInflater().inflate(R.menu.menu_foto, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id==R.id.seleccionar_galeria)
                        {
                            PermissionUtil.requestPermission(ActualizarUsuario.this, Manifest.permission.READ_EXTERNAL_STORAGE,"Es para el funcionamiento",IMAGE_PICKER_REQUEST);
                            abrirGaleria();
                        }
                        else if(id==R.id.usar_camara)
                        {
                            PermissionUtil.requestPermission(ActualizarUsuario.this, Manifest.permission.CAMERA,"Es para el funcionamiento",REQUEST_IMAGE_CAPTURE);
                            tomarFoto();
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        actualizarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Actualizacion de perfil",Toast.LENGTH_SHORT).show();
                if(imgbandera){
                    subirImagen();
                }

                persistUserData();
                finish();

            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void persistUserData(){
        UserData userData = getUserData();
        firebaseFirestore.collection("usuarios").document(firebaseAuth.getUid()).set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("!!!Usuario", "DocumentSnapshot successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Log.w("!!!Usuario", "Error adding document", e);
            }
        });
    }

    private UserData getUserData(){
        String nombre = nombres.getText().toString();
        String apellido = apellidos.getText().toString();
        String usuario = nombreUsuario.getText().toString();
        String celulars = celular.getText().toString();
        String direccions = direccion.getText().toString();
        String ciudads = ciudad.getText().toString();
        String barrios = barrio.getText().toString();
        return new UserData(nombre,apellido,usuario,tipous,celulars,direccions,ciudads,barrios);
    }

    public void initData(){
        firebaseFirestore.collection("usuarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(firebaseAuth.getUid())){
                                    UserData us = document.toObject(UserData.class);
                                    nombreUsuario.setText(us.getUsuario());
                                    nombres.setText(us.getNombre());
                                    apellidos.setText(us.getApellido());
                                    email.setText(firebaseAuth.getCurrentUser().getEmail());
                                    celular.setText(us.getCelular());
                                    direccion.setText(us.getDireccion());
                                    ciudad.setText(us.getCiudad());
                                    barrio.setText(us.getBarrio());
                                    tipous = us.getTipoUsuario();

                                }
                                Log.i("jaja", document.getId() + " => " + document.toObject(UserData.class));
                            }
                        } else {
                            Log.i("jaja", "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    private void cargarImagen(String key){
        try {
            Log.i("jaja","entro a imagen");
            File localFile = File.createTempFile(key, "jpg");
            ImageServiceFunction function = (params) -> {
                imgPerfil.setImageIcon(Icon.createWithFilePath(localFile.getPath()));
            };
            ImageService.downloadImage(IMAGE_ROUTE+key,localFile,function);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void subirImagen(){
        ImageServiceFunction function = (params) -> {
            Toast.makeText(ActualizarUsuario.this,"Datos actualizados corectamente",Toast.LENGTH_SHORT);
        };
        ImageService.uploadImage(IMAGE_ROUTE+firebaseAuth.getUid(),img_perfil,function);
    }

    private void tomarFoto() {
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null)
            {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void abrirGaleria() {
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                tomarFoto();
                return;
            case IMAGE_PICKER_REQUEST:
                abrirGaleria();
                return;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case IMAGE_PICKER_REQUEST:
                if(resultCode==RESULT_OK)
                {
                    try{
                        imgbandera = true;
                        final Uri imageUri= data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage= BitmapFactory.decodeStream(imageStream);
                        img_perfil = imageUri;
                        imgPerfil.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return;
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode==RESULT_OK)
                {
                    imgbandera = true;
                    Bundle extras =data.getExtras();
                    Bitmap imageBitmap=(Bitmap)extras.get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), imageBitmap, "Title", null);
                    img_perfil = Uri.parse(path);
                    imgPerfil.setImageBitmap(imageBitmap);
                }
                return;
        }
        setContentView(R.layout.activity_crear_editar_mascota);
    }
}