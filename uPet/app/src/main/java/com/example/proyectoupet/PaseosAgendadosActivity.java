package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class PaseosAgendadosActivity extends AppCompatActivity {
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
        List<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,                         android.R.layout.simple_spinner_item, arrayList);

        listView.setAdapter(adapter);
        setSupportActionBar(toolbar);
        verSolButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                openSolicitantesActivity();
            }
        });
        arrayList.add("Fecha");
        arrayList.add("2 de Septiembre");
        arrayList.add("3 de Septiembre");
        arrayList.add("4 de Septiembre");
        arrayList.add("5 de Septiembre");
        arrayList.add("6  de Septiembre");
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.perfil)
        {
            Toast.makeText(getApplicationContext(),"Click Perfil",Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.mascotas)
        {
            Toast.makeText(getApplicationContext(),"Click Mascotas",Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}