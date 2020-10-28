package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class PaseosAgendadosActivity extends AppCompatActivity {

    Button botonFiltroFecha;

    String[] solicitantes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paseos_agendados);
        initHoras();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,solicitantes);
        ListView listView = (ListView) findViewById(R.id.list_view);
        Button verSolButton = (Button) findViewById(R.id.buttonConfirmar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Spinner spinner = findViewById(R.id.spinner);
        listView.setAdapter(adapter);
        setSupportActionBar(toolbar);
        verSolButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                openSolicitantesActivity();
            }
        });
        botonFiltroFecha = findViewById(R.id.botonFiltroFecha);
    }

    public void selectDateSolicitantes(View v){
        Calendar cr = Calendar.getInstance();
        int day = cr.get(Calendar.DAY_OF_MONTH);
        int month = cr.get(Calendar.MONTH);
        int year = cr.get(Calendar.YEAR);
        new DatePickerDialog(PaseosAgendadosActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int nYear, int nMonth, int nDay) {
                botonFiltroFecha.setText(nDay+"/"+nMonth+"/"+nYear);
            }
        }, year,month,day).show();
    }

    public void openSolicitantesActivity()
    {
        Intent intent = new Intent(this, SeleccionarSolicitantesActitity.class );
        startActivity(intent);
    }
    private void initHoras()
    {
        solicitantes = new String[20];
        for(int i= 0; i < solicitantes.length; i++)
        {
            if(i%2==0)
            {
                solicitantes[i]="10 AM";
            }
            else
            {
                solicitantes[i]="10 PM";
            }
        }
    }

}