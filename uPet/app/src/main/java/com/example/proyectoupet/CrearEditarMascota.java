package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class CrearEditarMascota extends AppCompatActivity {

    private static final int IMAGE_PICKER_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    EditText nombreMascota;
    EditText edad;
    EditText especie;
    EditText raza;
    Button crear;
    ImageButton seleccion_imagen;
    ImageView imagen_mascota;

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
        imagen_mascota = findViewById(R.id.imagen_mascota);

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
                    imagen_mascota.setImageBitmap(imageBitmap);
                }
                return;
        }
        setContentView(R.layout.activity_crear_editar_mascota);
    }

}