package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class PaseosDisponibles extends AppCompatActivity {
    Spinner spinnertiposlugar;
    String[] arreglo;
    Button botDetallePaseo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paseos_disponibles);
        spinnertiposlugar = findViewById(R.id.spinnertiposlugar);
        spinnertiposlugar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String valor = (String) spinnertiposlugar.getSelectedItem();
                Toast.makeText(getBaseContext(),valor,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        initArray();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,arreglo);
        ListView listView = (ListView) findViewById(R.id.listaTipoLugares);
        listView.setAdapter(adapter);

        botDetallePaseo = findViewById(R.id.boton_detallePaseo);
        botDetallePaseo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(),DetallePaseoCliente.class));
            }
        });
    }

    private void initArray(){
        arreglo = new String[30];
        for (int i = 0; i<arreglo.length ; i++){
                arreglo[i] = "Paseo " + i;
        }
    }
}