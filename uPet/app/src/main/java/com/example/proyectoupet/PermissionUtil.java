package com.example.proyectoupet;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
public class PermissionUtil {
    public static void requestPermission(Activity context,String permission,String justification,int idCode){
        if(ContextCompat.checkSelfPermission(context,permission)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(context, permission)){
                Toast.makeText(context,justification,Toast.LENGTH_SHORT);
            }
            ActivityCompat.requestPermissions(context,new String[]{permission},idCode);
        }
    }
}
