package com.example.proyectoupet.services.background;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.proyectoupet.R;
import com.example.proyectoupet.UsuarioAdministrarPaseosActivity;
import com.example.proyectoupet.model.MascotaPuntoRecogida;
import com.example.proyectoupet.model.PaseoUsuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Random;

public class CambioSolicitudPaseos extends Service {

    public static final String CHANNEL_ID= "AVALIS";
    private boolean first = true;
    FirebaseFirestore firebaseFirestore;
    Random random;

    @Override
    public void onCreate() {
        super.onCreate();
        random = new Random();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("paseosUsuario");
        collectionReference.whereEqualTo("idUsuario",firebaseAuth.getUid()).addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        System.out.println("!!!!!-----va la madre");
                        if(!value.getDocuments().isEmpty()) {
                            PaseoUsuario paseoUsuario = value.getDocuments().get(0).toObject(PaseoUsuario.class);
                            if (PublicDataNameSpace.primeraVezConsulta) {
                                for (String idPasAcep : paseoUsuario.getPaseosAgendados()) {
                                    PublicDataNameSpace.paseosAceptadosId.add(idPasAcep);
                                }
                                for (String idPasCan : paseoUsuario.getPaseosCancelados()) {
                                    PublicDataNameSpace.paseosRechazadosId.add(idPasCan);
                                }
                                PublicDataNameSpace.primeraVezConsulta = false;
                            } else {
                                for (String idPasAcep : paseoUsuario.getPaseosAgendados()) {
                                    if (!PublicDataNameSpace.paseosAceptadosId.contains(idPasAcep)) {
                                        makeNotification(idPasAcep, "Solicitud de paseo aceptada!!!!", "Uno de los paseos que solicitaste fue aceptado");
                                        PublicDataNameSpace.paseosAceptadosId.add(idPasAcep);
                                    }
                                }
                                for (String idPasCanc : paseoUsuario.getPaseosCancelados()) {
                                    if (!PublicDataNameSpace.paseosRechazadosId.contains(idPasCanc)) {
                                        PublicDataNameSpace.paseosRechazadosId.add(idPasCanc);
                                        makeNotification(idPasCanc, "Solicitud de paseo cancelada :(", "Uno de los paseos que solicitaste fue cancelado");
                                    }
                                }
                            }
                        }
                    }
                }
        );
    }

    private void makeNotification(String key, String title, String text){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.deleteNotificationChannel(CHANNEL_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Upet";
            String description = "Canal para notificaciones del upet";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.notico);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intent = new Intent(this, UsuarioAdministrarPaseosActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
        Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("putExtra",key);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m,mBuilder.build());
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
