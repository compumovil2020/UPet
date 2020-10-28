package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyectoupet.model.PlaceOfInteresType;

import java.util.HashMap;
import java.util.Map;

public class LugaresdeInteres extends AppCompatActivity {
    Spinner spinnertiposlugar;
    String[] arreglo;
    Button botMapa;
    Map<PlaceOfInteresType,Boolean> activeTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugares_interes);
        initArray();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice ,arreglo);
        ListView listView = findViewById(R.id.listaTipoLugares);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        activeTypes.put(PlaceOfInteresType.VETERINAY,!activeTypes.get(PlaceOfInteresType.VETERINAY));
                        break;

                    case 1:
                        activeTypes.put(PlaceOfInteresType.PETSTORE,!activeTypes.get(PlaceOfInteresType.PETSTORE));
                        break;

                    case 2:
                        activeTypes.put(PlaceOfInteresType.PARK,!activeTypes.get(PlaceOfInteresType.PARK));
                        break;
                }
            }
        });

    }

    public void toLugaresdeInteresMap(View v){
        Intent i = new Intent(this,LugaresInteresMap.class);
        i.putExtra(PlaceOfInteresType.PETSTORE.name(),activeTypes.get(PlaceOfInteresType.PETSTORE));
        i.putExtra(PlaceOfInteresType.VETERINAY.name(),activeTypes.get(PlaceOfInteresType.VETERINAY));
        i.putExtra(PlaceOfInteresType.PARK.name(),activeTypes.get(PlaceOfInteresType.PARK));
        startActivity(i);
    }

    private void initArray(){
        arreglo = new String[]{"Veterinarias","Tiendas de mascotas","Parques"};
        activeTypes = new HashMap<>();
        activeTypes.put(PlaceOfInteresType.PETSTORE,false);
        activeTypes.put(PlaceOfInteresType.VETERINAY,false);
        activeTypes.put(PlaceOfInteresType.PARK,false);
    }


}