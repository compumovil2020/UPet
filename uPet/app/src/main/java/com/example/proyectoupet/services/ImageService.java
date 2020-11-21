package com.example.proyectoupet.services;

import android.graphics.drawable.Icon;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class ImageService {

    public static  void uploadImage(String route, Uri file, ImageServiceFunction function, Object... params){
        StorageReference riversRef = FirebaseStorage.getInstance().getReference().child(route);
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        function.execute(params);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
    }

    /**
     * recibe el nombre del archivo un archivo que sirve como buffer, la funcion a ser ejecutada en el caso de exito y los parametros
     * que seran usados porla funcion*/
    public static void downloadImage(String fileRoute,File localFile, ImageServiceFunction function, Object... params){
        try {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(fileRoute);
            imageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            function.execute(params);
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
}
