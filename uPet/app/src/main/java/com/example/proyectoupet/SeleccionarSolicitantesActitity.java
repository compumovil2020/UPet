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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SeleccionarSolicitantesActitity extends AppCompatActivity
{
    String[] solicitantes;
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
        Spinner spinner = findViewById(R.id.spinner);
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Fecha");
        arrayList.add("2 de Septiembre");
        arrayList.add("3 de Septiembre");
        arrayList.add("4 de Septiembre");
        arrayList.add("5 de Septiembre");
        arrayList.add("6  de Septiembre");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,                         android.R.layout.simple_spinner_item, arrayList);
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
    public void openDetalleSolicitanteActivity()
    {
        Intent intent = new Intent(this, SeleccionarDetalleSolicitanteActivity.class );
        startActivity(intent);
    }
    private void initSolicitantes()
    {
        solicitantes = new String[30];
        for(int i= 0; i < solicitantes.length; i++)
        {
            if(i%2==0)
            {
                solicitantes[i]="Paco";
            }
            else
            {
                solicitantes[i]="Lola";
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
