package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ListaMascotas extends AppCompatActivity {

    Button editar_crear;
    String[] arreglo;
    ListView listaMascotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mascotas);
        initArray();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.item_list_fuente, arreglo);
        listaMascotas = (ListView) findViewById(R.id.listaMascotas);
        listaMascotas.setAdapter(adapter);

        editar_crear = findViewById(R.id.buttonEditarMascota);

        editar_crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), CrearEditarMascota.class));
            }
        });

        listaMascotas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getBaseContext(), PerfilMascota.class));
            }
        });



    }
    private void initArray() {
        arreglo = new String[2];
        for (int i = 0; i < arreglo.length; i++)
            if (i % 2 == 0)
                arreglo[i] = "Manchas";
            else
                arreglo[i] = "Pecas";
    }

}