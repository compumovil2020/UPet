package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proyectoupet.model.Mascota;
import com.example.proyectoupet.services.ImageService;
import com.example.proyectoupet.services.ImageServiceFunction;

import java.io.File;
import java.io.IOException;

public class VerDetalleMascotaActivity extends AppCompatActivity {

    public static final String IMAGE_ROUTE = "fotos_mascotas/";

    private TextView nombreMascota;

    private TextView especieMascota;

    private TextView razaMascota;

    private TextView edadMascota;

    private ImageView imagen_mascota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_detalle_mascota);
        Mascota mascota = (Mascota) getIntent().getExtras().get("mascota");
        String mascotaId = getIntent().getExtras().getString("mascotaId");
        nombreMascota = findViewById(R.id.nombreMascotaVerDetalle);
        especieMascota = findViewById(R.id.editTextEspecieMascota);
        razaMascota = findViewById(R.id.editTextRazaMascota);
        edadMascota = findViewById(R.id.editTextEdadMascota);
        imagen_mascota = findViewById(R.id.editImagenMascotaVerDetalle);

        nombreMascota.setText(mascota.getNombreMascota());
        especieMascota.setText(mascota.getEspecie());
        razaMascota.setText(mascota.getRaza());
        edadMascota.setText(mascota.getEdad()+"");
        cargarImagen(mascotaId);


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


    public void volverListar(View v)
    {
        finish();
    }
}