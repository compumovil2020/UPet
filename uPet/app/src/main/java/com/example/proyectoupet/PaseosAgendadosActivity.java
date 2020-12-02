package com.example.proyectoupet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoupet.model.Mascota;
import com.example.proyectoupet.model.Paseo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;


public class PaseosAgendadosActivity extends AppCompatActivity {

    private Button botonFiltroFecha;
    private TextView textFecha;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    private int pos;

    private List<String> solicitantes;

    private List<String> idPaseosAgendados;

    private ListView listView;

    private View previousView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paseos_agendados);
        db = FirebaseFirestore.getInstance();
        pos = -1;
        firebaseAuth = FirebaseAuth.getInstance();
        listView = (ListView) findViewById(R.id.paseosAgendadosPaseador);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long arg3) {
                view.setBackgroundColor(getResources().getColor(R.color.greenPet,null));
                if(pos != position)
                {
                    if(previousView != null){
                        previousView.setBackgroundColor(getResources().getColor(R.color.beigePet,null));
                    }
                }
                pos = position;
                previousView = view;
            }
        });
        initHoras();
        Button verSolButton = (Button) findViewById(R.id.btnVerSolicitantesPaseosAgendados);
        Toolbar toolbar = findViewById(R.id.toolbar);
        textFecha = findViewById(R.id.textoFechaPaseosAgendados);
        setSupportActionBar(toolbar);
        verSolButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Log.w("We help","chicken");
                if(pos != -1)
                {
                    openSolicitantesActivity(pos);
                }

            }
        });
        botonFiltroFecha = findViewById(R.id.botonFiltroFecha);
    }

    public void filtrarPorFecha(View v){
        if(!textFecha.getText().toString().isEmpty())
        {
            filtrarFechas();
        }
        else
        {
            Toast.makeText(this, "Seleccione una fecha primero", Toast.LENGTH_LONG);
        }
    }

    public void selectDateSolicitantes(View v){
        Calendar cr = Calendar.getInstance();
        int day = cr.get(Calendar.DAY_OF_MONTH);
        int month = cr.get(Calendar.MONTH);
        int year = cr.get(Calendar.YEAR);
        new DatePickerDialog(PaseosAgendadosActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int nYear, int nMonth, int nDay) {
                textFecha.setText(nDay+"/"+(nMonth+1)+"/"+nYear);
            }
        }, year,month,day).show();
    }

    public void openSolicitantesActivity(int id)
    {
        Intent intent = new Intent(this, SeleccionarSolicitantesActitity.class );
        intent.putExtra("idPaseo",idPaseosAgendados.get(id));
        startActivity(intent);
    }

    private void filtrarFechas()
    {
        db.collection("paseosAgendados").whereEqualTo("userId",firebaseAuth.getUid()).whereEqualTo("fecha",textFecha.getText().toString()).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        solicitantes = new ArrayList<>();
                        idPaseosAgendados = new ArrayList<>();
                        for(DocumentSnapshot document : value.getDocuments()){
                            idPaseosAgendados.add(document.getId());
                            Paseo p = document.toObject(Paseo.class);
                            solicitantes.add("Paseo: " +p.getFecha() +" "+p.getHoraInicio()+"-"+p.getHoraFin());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PaseosAgendadosActivity.this,android.R.layout.simple_list_item_1,solicitantes);
                        listView.setAdapter(adapter);
                    }
                });
    }

    private void initHoras()
    {
        db.collection("paseosAgendados").whereEqualTo("userId",firebaseAuth.getUid()).
                    addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            solicitantes = new ArrayList<>();
                            idPaseosAgendados = new ArrayList<>();
                            for(DocumentSnapshot document : value.getDocuments()){
                                idPaseosAgendados.add(document.getId());
                                Paseo p = document.toObject(Paseo.class);
                                solicitantes.add("Paseo: " +p.getFecha() +" "+p.getHoraInicio()+"-"+p.getHoraFin());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(PaseosAgendadosActivity.this,android.R.layout.simple_list_item_1,solicitantes);
                            listView.setAdapter(adapter);
                        }
                    });
    }

}