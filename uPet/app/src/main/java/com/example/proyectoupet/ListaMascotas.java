package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ListaMascotas extends AppCompatActivity {

    Button editar_crear;
    Button eliminar;
    String[] arreglo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mascotas);
        initArray();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arreglo);
        ListView listView = (ListView) findViewById(R.id.listaMascotas);
        listView.setAdapter(adapter);

        editar_crear = findViewById(R.id.buttonEditarMascota);
        eliminar = findViewById(R.id.buttonEliminarMascota);

        editar_crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), CrearEditarMascota.class));
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Eliminado",Toast. LENGTH_SHORT).show();
            }
        });
    }
    private void initArray() {
        arreglo = new String[30];
        for (int i = 0; i < arreglo.length; i++)
            if (i % 2 == 0)
                arreglo[i] = "hola";
            else
                arreglo[i] = "mascotas";
    }

}