package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ActualizarUsuario extends AppCompatActivity {

    final int REQUEST_IMAGE_CAPTURE=1;
    final int IMAGE_PICKER_REQUEST=100;
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
    ImageView imgPerfil;
    ImageView imgEdit;

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
        imgPerfil = findViewById(R.id.imageViewPerfil);
        imgEdit = findViewById(R.id.imageView5);

        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission((Activity)v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE,"Para ver galería",IMAGE_PICKER_REQUEST);
                if(ContextCompat.checkSelfPermission(v.getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                    //Toast.makeText(v.getContext(),"Negado, no se puede hacer nada",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent pickImage=new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/");
                    startActivityForResult(pickImage,IMAGE_PICKER_REQUEST);
                }
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission((Activity)v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE,"Para ver galería",IMAGE_PICKER_REQUEST);
                if(ContextCompat.checkSelfPermission(v.getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                    //Toast.makeText(v.getContext(),"Negado, no se puede hacer nada",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent pickImage=new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/");
                    startActivityForResult(pickImage,IMAGE_PICKER_REQUEST);
                }
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case IMAGE_PICKER_REQUEST:
                if(resultCode==RESULT_OK){
                    try{
                        final Uri imageUri=data.getData();
                        final InputStream imageStream=getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage= BitmapFactory.decodeStream(imageStream);
                        imgPerfil.setImageBitmap(selectedImage);
                    }
                    catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode==RESULT_OK){
                    Bundle extras=data.getExtras();
                    Bitmap imageBitmap=(Bitmap)extras.get("data");
                    imgPerfil.setImageBitmap(imageBitmap);
                }
        }
    }

    private void requestPermission(Activity context, String permiso, String justificacion, int idCode){
        if(ContextCompat.checkSelfPermission(context, permiso)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)){
                Toast.makeText(context,justificacion,Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context,new String[]{permiso},idCode);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[],int[] grantResults){
        switch(requestCode){
            case IMAGE_PICKER_REQUEST:{
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Intent pickImage=new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/");
                    startActivityForResult(pickImage,IMAGE_PICKER_REQUEST);
                }else{
                    Toast.makeText(this,"Funcionalidad limitada",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

}