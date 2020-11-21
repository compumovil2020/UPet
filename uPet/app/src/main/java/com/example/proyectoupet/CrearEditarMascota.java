package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.proyectoupet.model.Mascota;
import com.example.proyectoupet.services.ImageService;
import com.example.proyectoupet.services.ImageServiceFunction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CrearEditarMascota extends AppCompatActivity implements Validator.ValidationListener{

    private static final int IMAGE_PICKER_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    public static final String IMAGE_ROUTE = "fotos_mascotas/";

    private FirebaseAuth firebaseAut;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference mStorageRef;
    @NotEmpty(message = "La mascota debe tener un nombre")
    EditText nombreMascota;
    @NotEmpty(message = "La mascota debe tener una edad")
    EditText edad;
    EditText especie;
    EditText raza;
    Button botonGuardarMascota;
    ImageButton seleccion_imagen;
    ImageView imagen_mascota;

    Uri imagenMascota;

    Boolean crear;

    Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_editar_mascota);


        Bundle extras = getIntent().getExtras();
        crear = extras.getBoolean("crear");
        firebaseAut = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        nombreMascota = findViewById(R.id.editTextNombreMascota);
        edad = findViewById(R.id.editTextEdadMascota);
        especie = findViewById(R.id.editTextEspecie);
        raza = findViewById(R.id.editTextRaza);
        botonGuardarMascota = findViewById(R.id.botonGuardarMascota);
        seleccion_imagen =  findViewById(R.id.botonSeleccionImagen);
        imagen_mascota = findViewById(R.id.editImagenMascota);

        initFields();
        seleccion_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(CrearEditarMascota.this, seleccion_imagen);
                popup.getMenuInflater().inflate(R.menu.menu_foto, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id==R.id.seleccionar_galeria)
                        {
                            PermissionUtil.requestPermission(CrearEditarMascota.this, Manifest.permission.READ_EXTERNAL_STORAGE,"Es para el funcionamiento",IMAGE_PICKER_REQUEST);
                            abrirGaleria();
                        }
                        else if(id==R.id.usar_camara)
                        {
                            PermissionUtil.requestPermission(CrearEditarMascota.this, Manifest.permission.CAMERA,"Es para el funcionamiento",REQUEST_IMAGE_CAPTURE);
                            tomarFoto();
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    private void initFields(){
        if(!crear){
            Mascota m = (Mascota) getIntent().getExtras().get("mascota");
            String mascodaId = getIntent().getExtras().getString("mascotaId");
            cargarImagen(mascodaId);
            nombreMascota.setText(m.getNombreMascota());
            edad.setText(m.getEdad().toString());
            raza.setText(m.getRaza());
            especie.setText(m.getEspecie());
        }
    }

    private void cargarImagen(String key){
        try {
            File localFile = File.createTempFile(key, "jpg");
            ImageServiceFunction function = (params) -> {
                imagen_mascota.setImageIcon(Icon.createWithFilePath(localFile.getPath()));
            };
            ImageService.downloadImage(IMAGE_ROUTE+key,localFile,function);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void crearEditarMascotaSubmit(View v){
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        Mascota datosMascota = getDatosMascota();
        if(crear){
            firebaseFirestore.collection("mascotas").add(datosMascota).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    if(imagenMascota != null) {
                        subirImagen(documentReference.getId(), datosMascota);
                    }
                }
            });

        }else{
            String mascotaId = getIntent().getExtras().getString("mascotaId");
            firebaseFirestore.collection("mascotas").document(mascotaId).set(datosMascota);
            if(imagenMascota != null){
                subirImagen(mascotaId,datosMascota);
            }
        }
    }

    private void subirImagen(String mascotaId, Mascota datosMascota){
        ImageServiceFunction function = (params) -> {
            Toast.makeText(CrearEditarMascota.this,"Datos actualizados corectamente",Toast.LENGTH_SHORT);
            startActivity(new Intent(CrearEditarMascota.this,PerfilMascota.class).setFlags
                    (Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("mascota",datosMascota).putExtra("mascotaId",mascotaId));
        };
        ImageService.uploadImage("fotos_mascotas/"+mascotaId,imagenMascota,function);
    }

    private Mascota getDatosMascota(){
        String userId = firebaseAut.getUid();
        String nombre = nombreMascota.getText().toString();
        Integer ed = Integer.parseInt(edad.getText().toString());
        String esp = especie.getText().toString();
        String raz = raza.getText().toString();
        return new Mascota(userId,nombre,ed,esp,raz);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case IMAGE_PICKER_REQUEST:
                if(resultCode==RESULT_OK)
                {
                    try{
                        final Uri imageUri= data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage= BitmapFactory.decodeStream(imageStream);
                        imagenMascota = imageUri;
                        imagen_mascota.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return;
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode==RESULT_OK)
                {
                    Bundle extras =data.getExtras();
                    Bitmap imageBitmap=(Bitmap)extras.get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), imageBitmap, "Title", null);
                    imagenMascota = Uri.parse(path);
                    imagen_mascota.setImageBitmap(imageBitmap);
                }
                return;
        }
        setContentView(R.layout.activity_crear_editar_mascota);
    }

}