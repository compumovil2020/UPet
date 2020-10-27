package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.proyectoupet.services.CustomListviewMascotasCheckboxAdapter;

import java.util.List;

public class UsuarioSeleccionarMascotasActivity extends AppCompatActivity {

    private String[] nombresPerros={"Becquer","Elias","Lune","Paco","Laika"};
    private int perros[] = {R.drawable.perrito, R.drawable.perro_elias, R.drawable.perro_lune, R.drawable.pug, R.drawable.pug};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_seleccionar_mascotas);
        Button btnContinuar = findViewById(R.id.btnContinuarSM);
        btnContinuar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String punto = "¡Paseo Solicitado!";
                Toast.makeText(getBaseContext(),punto, Toast.LENGTH_LONG);
                Intent intent = new Intent(getBaseContext(), UsuarioAdministrarPaseosActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button btnVolver = findViewById(R.id.btnVolverSM);
        btnVolver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

        ListView listaPerros = findViewById(R.id.listaUMascotas);
        String[] nombrePerros2 = obtenerNombresPerros();
        int perros2[] = obtenerPerros();
        CustomListviewMascotasCheckboxAdapter adapter = new CustomListviewMascotasCheckboxAdapter(this,perros2,nombrePerros2);
        listaPerros.setAdapter(adapter);
        listaPerros.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String o = (String) parent.getItemAtPosition(position);
                Toast.makeText(getBaseContext(),o,Toast.LENGTH_SHORT);
            }
        });

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

    public int[] obtenerPerros()
    {
        int[] perros = new int[3];
        int random;
        random = (int)(Math.random() * 5);
        perros[0] = this.perros[random];
        random = (int)(Math.random() * 5);
        perros[1] = this.perros[random];
        random = (int)(Math.random() * 5);
        perros[2] = this.perros[random];

        return perros;
    }
}