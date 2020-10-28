package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class UsuarioAdministrarPaseosActivity extends AppCompatActivity {


    private Button btnVerDetalles;
    private Button btnBuscarPaseo;
    private Button btnVolver;
    private ListView listaPaseos;
    private String paseoSeleccionado;
    private int positionPaseoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_administrar_paseos);
        btnVerDetalles = findViewById(R.id.btnVerDetallesAP);
        btnBuscarPaseo = findViewById(R.id.btnBuscarPaseoAP);
        btnVolver = findViewById(R.id.btnVolverAP);
        btnVerDetalles.setEnabled(false);
        List<String> paseos = obtenerPaseos();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_single_choice,paseos);
        listaPaseos = findViewById(R.id.listaUPaseos);
        listaPaseos.setChoiceMode(listaPaseos.CHOICE_MODE_SINGLE);
        listaPaseos.setAdapter(adapter);
        listaPaseos.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btnVerDetalles.setEnabled(true);
                paseoSeleccionado = (String) listaPaseos.getItemAtPosition(position);
                positionPaseoSeleccionado = position;
            }
        });

        btnVerDetalles.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getBaseContext(),UsuarioVerDetallesPaseo.class);
                String tipoRuta = "2";
                if(positionPaseoSeleccionado == 1)
                {
                    tipoRuta = "3";
                }
                else if(positionPaseoSeleccionado == 2)
                {
                    tipoRuta = "5";
                }
                intent.putExtra("TIPO_RUTA",tipoRuta);
                startActivity(intent);
            }
        });

        btnBuscarPaseo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getBaseContext(),UsuarioBuscarPaseoActivity.class);
                String tipoRuta = "2";
                if(positionPaseoSeleccionado == 1)
                {
                    tipoRuta = "3";
                }
                else if(positionPaseoSeleccionado == 2)
                {
                    tipoRuta = "5";
                }
                intent.putExtra("TIPO_RUTA",tipoRuta);
                startActivity(intent);
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finishActivity(0);
            }
        });

    }
    public List<String> obtenerPaseos()
    {
        List<String> paseos = new ArrayList<String>();
        paseos.add("Paseo Miercoles 11 Noviembre 8:00AM - Ricardo Arjona");
        paseos.add("Paseo Miercoles 18 Noviembre 8:00AM - Ricardo Arjona");
        paseos.add("Paseo Miercoles 25 Noviembre 8:00AM - Ricardo Arjona");
        return paseos;
    }
}