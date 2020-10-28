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

import com.example.proyectoupet.paseos.CrearPaseo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SeleccionarSolicitantesActitity extends AppCompatActivity
{
    String[] solicitantes;
    private String[] nombresPerros={"Becquer","Elias","Lune","Paco","Laika"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_solicitantes);
        initSolicitantes();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,solicitantes);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                openDetalleSolicitanteActivity();
                return true;
            }
        });
        listView.setAdapter(adapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    public void openDetalleSolicitanteActivity()
    {
        Intent intent = new Intent(this, SeleccionarDetalleSolicitanteActivity.class );
        startActivity(intent);
    }
    private void initSolicitantes()
    {
        solicitantes = obtenerNombresPerros();
    }

    public String[] obtenerNombresPerros()
    {
        String[] nombresPerros = new String[3];
        int random;
        random = (int)(Math.random() * 5);
        nombresPerros[0] = this.nombresPerros[random];
        random = (int)(Math.random() * 5);
        nombresPerros[1] = this.nombresPerros[random];
        random = (int)(Math.random() * 5);
        nombresPerros[2] = this.nombresPerros[random];

        return nombresPerros;
    }

    public void confirmarSolicitantes(View v){
        finish();
    }

    public void cancelarSolicitantes(View v){
        finish();
    }

}
