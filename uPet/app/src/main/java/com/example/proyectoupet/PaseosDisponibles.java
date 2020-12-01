package com.example.proyectoupet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class PaseosDisponibles extends AppCompatActivity {

    private TextView textFecha;

    private List<String> paseos;

    private List<String> idPaseosAgendados;

    private int pos;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    private ListView listView;

    private View previousView;

    private Button botDetallePaseo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paseos_disponibles);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        textFecha = findViewById(R.id.txtFechaFiltradasDisp);
        listView = (ListView) findViewById(R.id.listaPaseosDisp);
        pos = -1;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long arg3) {
                view.setBackgroundColor(getResources().getColor(R.color.greenPet,null));

                if(pos == position)
                {
                    previousView.setBackgroundColor(getResources().getColor(R.color.beigePet,null));
                }
                pos = position;
                previousView = view;
            }
        });
        initArray();

        botDetallePaseo = findViewById(R.id.boton_detallePaseoDisp);
        botDetallePaseo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if(pos != -1)
                {
                    buscarPaseo(pos);
                }

            }
        });

    }

    private void initArray(){
        db.collection("paseosAgendados").
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        paseos = new ArrayList<>();
                        idPaseosAgendados = new ArrayList<>();
                        for(DocumentSnapshot document : value.getDocuments()){
                            idPaseosAgendados.add(document.getId());
                            Paseo p = document.toObject(Paseo.class);
                            paseos.add("Paseo: " +p.getFecha() +" "+p.getHoraInicio()+"-"+p.getHoraFin());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PaseosDisponibles.this,android.R.layout.simple_list_item_1,paseos);
                        listView.setAdapter(adapter);
                    }
                });
    }

    public void buscarPaseo(int id){
        Intent intent = new Intent(this, UsuarioBuscarPaseoActivity.class );
        intent.putExtra("idPaseo",idPaseosAgendados.get(id));
        if(textFecha.getText().toString().isEmpty())
        {
            intent.putExtra("fecha","No");
        }
        else
        {
            intent.putExtra("fecha",textFecha.getText().toString());
        }
        startActivity(intent);
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
        new DatePickerDialog(PaseosDisponibles.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int nYear, int nMonth, int nDay) {
                textFecha.setText(nDay+"/"+(nMonth+1)+"/"+nYear);
            }
        }, year,month,day).show();
    }

    private void filtrarFechas()
    {
        db.collection("paseosAgendados")
                .whereEqualTo("fecha",textFecha.getText().toString()).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        paseos = new ArrayList<>();
                        idPaseosAgendados = new ArrayList<>();
                        for(DocumentSnapshot document : value.getDocuments()){
                            idPaseosAgendados.add(document.getId());
                            Paseo p = document.toObject(Paseo.class);
                            paseos.add("Paseo: " +p.getFecha() +" "+p.getHoraInicio()+"-"+p.getHoraFin());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PaseosDisponibles.this,android.R.layout.simple_list_item_1,paseos);
                        listView.setAdapter(adapter);

                    }
                });
    }
}