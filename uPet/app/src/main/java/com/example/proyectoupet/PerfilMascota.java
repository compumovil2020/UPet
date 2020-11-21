package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoupet.model.Mascota;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class PerfilMascota extends AppCompatActivity {
    public static final String IMAGE_ROUTE = "fotos_mascotas/";
    TextView nombreMascota;
    TextView edad;
    TextView especie;
    TextView raza;
    Button botoneditar;
    Button botonborrar;
    ImageView imagen_mascota;
    AlertDialog dialog;

    Mascota mascota;
    String mascotaId;

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_mascota);
        firebaseFirestore = FirebaseFirestore.getInstance();

        nombreMascota = findViewById(R.id.showTextNombreMascota);
        edad = findViewById(R.id.showTextEdadMascota);
        especie = findViewById(R.id.showTextEspecie);
        raza = findViewById(R.id.showTextRaza);
        botoneditar = findViewById(R.id.botonEditarMascota);
        botonborrar =  findViewById(R.id.botonBorrarMascota);
        imagen_mascota = findViewById(R.id.showImagenMascota);

        mascota = (Mascota) getIntent().getExtras().get("mascota");
        mascotaId = getIntent().getExtras().getString("mascotaId");
        nombreMascota.setText(mascota.getNombreMascota());
        edad.setText(mascota.getEdad().toString());
        especie.setText(mascota.getEspecie());
        raza.setText(mascota.getRaza());
        cargarImagen(mascotaId);
    }

    private void cargarImagen(String key){
        try {
            File localFile = File.createTempFile(key, "jpg");
            StorageReference imageRef = FirebaseStorage.getInstance().getReference(IMAGE_ROUTE).child(key);
            imageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            imagen_mascota.setImageIcon(Icon.createWithFilePath(localFile.getPath()));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void borrarMascota(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Borrar Mascota");
        builder.setMessage("¿Esta seguro que quiere eliminar la mascota?");

        builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebaseFirestore.collection("mascotas").document(mascotaId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(PerfilMascota.this,ListaMascotas.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        Toast.makeText(getBaseContext(), "Borrado", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        builder.setNegativeButton("Cancelar", null);
        dialog = builder.create();
        dialog.show();
    }

    public void toEditarMascota(View v){
        startActivity(new Intent(getBaseContext(), CrearEditarMascota.class).putExtra("mascota",mascota)
                .putExtra("mascotaId",mascotaId).putExtra("crear",false));
    }
}