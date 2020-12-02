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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;

public class AvalibilityChecker extends Service {


    public static final String PATH_USER = "users/";
    public static final String CHANNEL_ID= "AVALIS";
    private boolean first = true;
    FirebaseFirestore firebaseFirestore;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android


        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +100, restartServicePI);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        databaseReference.orderByChild("avalible").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot single : snapshot.getChildren()){
                    UserData userData = single.getValue(UserData.class);
                    if(!PublicDataNameSpace.firstTimeFetchingUsers && !PublicDataNameSpace.activeUsers.contains(single.getKey())){
                        makeNotification(new String(single.getKey()),userData.getName());
                    }
                    PublicDataNameSpace.activeUsers.add(single.getKey());
                }
                PublicDataNameSpace.firstTimeFetchingUsers = false;
                PublicDataNameSpace.activeUsers = new HashSet<>();
                for(DataSnapshot single : snapshot.getChildren()){
                    PublicDataNameSpace.activeUsers.add(single.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public AvalibilityChecker(){
        databaseReference = FirebaseDatabase.getInstance().getReference(PATH_USER);

    }

    private void makeNotification(String key, String userName){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.deleteNotificationChannel(CHANNEL_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Taller3NCH";
            String description = "Canal para notificaciones del taller 3";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.notico);
        mBuilder.setContentTitle("Nuevo usuario disponible");
        mBuilder.setContentText("El usuario "+ userName+ " acabo de cambiar su estado a disponible");
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intent = new Intent(this, TrackingMapScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
        Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("userId",key);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        notificationManager.notify(1,mBuilder.build());
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
