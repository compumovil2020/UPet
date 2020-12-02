package com.example.proyectoupet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyectoupet.model.EstadoPaseo;
import com.example.proyectoupet.model.MascotaPuntoRecogida;
import com.example.proyectoupet.model.Paseo;
import com.example.proyectoupet.model.PaseoSolicitar;
import com.example.proyectoupet.model.PaseoUsuario;
import com.example.proyectoupet.model.UserData;
import com.example.proyectoupet.paseos.CrearPaseo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SeleccionarSolicitantesActitity extends AppCompatActivity
{
    private List<String> solicitantes;

    private List<MascotaPuntoRecogida> listaMascotas;

    private List<String> idPaseosSolicitantes;

    private String idPaseo;

    private ListView listaSolicitantes;

    private FirebaseFirestore db;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_solicitantes);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        idPaseo = getIntent().getExtras().getString("idPaseo");
        listaSolicitantes = findViewById(R.id.listaSolicitantesSeleccionar);
        initSolicitantes();
        listaSolicitantes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listaSolicitantes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                openDetalleSolicitanteActivity(i);
                return true;
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    public void openDetalleSolicitanteActivity(int pos)
    {
        Intent intent = new Intent(this, SeleccionarDetalleSolicitanteActivity.class );
        if(listaMascotas.size() >= pos)
        {
            intent.putExtra("idUsuario",listaMascotas.get(pos).getUsuarioId()).putExtra("idPaseo",idPaseo).putExtra("parada",listaMascotas.get(pos).getPuntoRecogida());
            startActivity(intent);
        }

    }
    private void initSolicitantes()
    {
        db.collection("paseosSolicitados").whereEqualTo("idPaseo",idPaseo).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        solicitantes = new ArrayList<>();
                        idPaseosSolicitantes = new ArrayList<>();
                        listaMascotas = new ArrayList<>();
                        PaseoSolicitar p = null;

                        for(DocumentSnapshot document : value.getDocuments()) {
                            p = document.toObject(PaseoSolicitar.class);
                            if (p != null) {
                                for (MascotaPuntoRecogida mpr : p.getMascotasPuntoRecogida()) {
                                    if (mpr.getEstado().equals(EstadoPaseo.SOLICITADO.toString())) {
                                        db.collection("usuarios").document(mpr.getUsuarioId()).get().addOnCompleteListener(
                                                new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        UserData ud = task.getResult().toObject(UserData.class);
                                                        solicitantes.add(ud.getNombre() + " " + ud.getApellido());
                                                        listaMascotas.add(mpr);
                                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SeleccionarSolicitantesActitity.this, android.R.layout.simple_list_item_1, solicitantes);
                                                        listaSolicitantes.setAdapter(adapter);
                                                    }
                                                });
                                    }


                                }
                            }
                        }
                    }
                });
    }



    public void confirmarSolicitantes(View v){
        SparseBooleanArray spa = listaSolicitantes.getCheckedItemPositions();
        for(int i = 0; i < spa.size(); i++)
        {
            if(spa.get(i))
            {
                MascotaPuntoRecogida mascotaPuntoRecogida = listaMascotas.get(i);
                db.collection("paseosSolicitados").whereEqualTo("idPaseo",idPaseo).get().addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    PaseoSolicitar p = null;
                                    String idPaseoSolicitar = "";
                                    for( QueryDocumentSnapshot dsPaseoSolicitado : task.getResult()) {
                                        p = dsPaseoSolicitado.toObject(PaseoSolicitar.class);
                                        idPaseoSolicitar = dsPaseoSolicitado.getId();
                                        if (p != null)
                                        {
                                            List<MascotaPuntoRecogida> nueva = new ArrayList<>();
                                            for(MascotaPuntoRecogida mpr: p.getMascotasPuntoRecogida())
                                            {
                                                if(mpr.getUsuarioId().equals(mascotaPuntoRecogida.getUsuarioId()))
                                                {
                                                    mpr.setEstado(EstadoPaseo.CONFIRMADO.toString());
                                                    nueva.add(mpr);
                                                    db.collection("paseosUsuario").whereEqualTo("idUsuario",mpr.getUsuarioId()).get().addOnSuccessListener(
                                                            new OnSuccessListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                    if(!queryDocumentSnapshots.getDocuments().isEmpty())
                                                                    {
                                                                        PaseoUsuario pu = queryDocumentSnapshots.getDocuments().get(0).toObject(PaseoUsuario.class);
                                                                        pu.getPaseosAgendados().add(idPaseo);
                                                                        db.collection("paseosUsuario").document(queryDocumentSnapshots.getDocuments().get(0).getId()).update("paseosAgendados",pu.getPaseosAgendados());
                                                                    }
                                                                    else{
                                                                        List<String> paseosA = new ArrayList<>();
                                                                        paseosA.add(idPaseo);
                                                                        List<String> paseosC = new ArrayList<>();
                                                                        PaseoUsuario pu = new PaseoUsuario(mpr.getUsuarioId(),paseosA,paseosC);
                                                                        db.collection("paseosUsuario").add(pu);
                                                                    }

                                                                }
                                                            }
                                                    ).addOnFailureListener(
                                                            new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                }
                                                            }
                                                    );
                                                }
                                                else
                                                {
                                                    nueva.add(mpr);
                                                }
                                            }
                                            p.setMascotasPuntoRecogida(nueva);
                                            db.collection("paseosSolicitados").document(idPaseoSolicitar).set(p);

                                        }
                                    }

                                }

                            }
                        });


            }
        }
    }

    public void cancelarSolicitantes(View v){
        SparseBooleanArray spa = listaSolicitantes.getCheckedItemPositions();
        for(int i = 0; i < spa.size(); i++)
        {
            if(spa.get(i))
            {
                MascotaPuntoRecogida mascotaPuntoRecogida = listaMascotas.get(i);
                db.collection("paseosSolicitados").whereEqualTo("idPaseo",idPaseo).get().addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    PaseoSolicitar p = null;
                                    String idPaseoSolicitar = "";
                                    for( QueryDocumentSnapshot dsPaseoSolicitado : task.getResult()) {
                                        p = dsPaseoSolicitado.toObject(PaseoSolicitar.class);
                                        idPaseoSolicitar = dsPaseoSolicitado.getId();
                                        if (p != null)
                                        {
                                            List<MascotaPuntoRecogida> nueva = new ArrayList<>();
                                            for(MascotaPuntoRecogida mpr: p.getMascotasPuntoRecogida())
                                            {
                                                if(mpr.getUsuarioId().equals(mascotaPuntoRecogida.getUsuarioId()))
                                                {
                                                    mpr.setEstado(EstadoPaseo.CANCELADO.toString());
                                                    nueva.add(mpr);
                                                    db.collection("paseosUsuario").whereEqualTo("idUsuario",mpr.getUsuarioId()).get().addOnSuccessListener(
                                                            new OnSuccessListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                    if(!queryDocumentSnapshots.getDocuments().isEmpty())
                                                                    {
                                                                        PaseoUsuario pu = queryDocumentSnapshots.getDocuments().get(0).toObject(PaseoUsuario.class);
                                                                        pu.getPaseosCancelados().add(idPaseo);
                                                                        db.collection("paseosUsuario").document(queryDocumentSnapshots.getDocuments().get(0).getId()).update("paseosAgendados",pu.getPaseosCancelados());
                                                                    }
                                                                    else{
                                                                        List<String> paseosA = new ArrayList<>();
                                                                        List<String> paseosC = new ArrayList<>();
                                                                        paseosC.add(idPaseo);
                                                                        PaseoUsuario pu = new PaseoUsuario(mpr.getUsuarioId(),paseosA,paseosC);
                                                                        db.collection("paseosUsuario").add(pu);
                                                                    }

                                                                }
                                                            }
                                                    ).addOnFailureListener(
                                                            new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                }
                                                            }
                                                    );
                                                }
                                                else
                                                {
                                                    nueva.add(mpr);
                                                }
                                            }
                                            p.setMascotasPuntoRecogida(nueva);
                                            db.collection("paseosSolicitados").document(idPaseoSolicitar).set(p);

                                        }
                                    }

                                }

                            }
                        });

            }
        }
    }

}
