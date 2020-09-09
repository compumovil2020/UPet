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

public class LugaresdeInteres extends AppCompatActivity {
    Spinner spinnertiposlugar;
    String[] arreglo;
    Button botMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugares_interes);
        spinnertiposlugar = findViewById(R.id.spinnertiposlugar);
        botMapa = findViewById(R.id.boton_verMapa);

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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice ,arreglo);
        ListView listView = (ListView) findViewById(R.id.listaTipoLugares);
        listView.setAdapter(adapter);

        botMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), MapView.class));
            }
        });
    }

    private void initArray(){
        arreglo = new String[30];
        for (int i = 0; i<arreglo.length ; i++){
            if(i%2 == 0){
                arreglo[i] = "hola";
            }else{
                arreglo[i] = "mundo";
            }
        }
    }


}