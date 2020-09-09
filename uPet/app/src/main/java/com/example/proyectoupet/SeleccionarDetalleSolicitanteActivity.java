package com.example.proyectoupet;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class SeleccionarDetalleSolicitanteActivity extends AppCompatActivity
{

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_detalle_solicitante);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        TextView texto = (TextView) findViewById(R.id.nombreMascotaText);
        Button botonCancelar = (Button) findViewById(R.id.buttonCancelar);
        Button botonConfirmar = (Button) findViewById(R.id.buttonConfirmar);
        texto.setText("#Nombre");
        texto =  (TextView)findViewById(R.id.tipoMascotaText);
        texto.setText("#Tipo");
        texto =  (TextView)findViewById(R.id.duenoText);
        texto.setText("#Nombre Dueño");
        texto =  (TextView)findViewById(R.id.direccionText);
        texto.setText("#Dirección");

        botonCancelar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                openCancelarSolicitante();
            }
        });
        botonConfirmar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                openConfirmarSolicitante();
            }
        });
    }

    public void openConfirmarSolicitante()
    {
        Intent intent = new Intent(this, SeleccionarSolicitantesActitity.class );
        startActivity(intent);
    }

    public void openCancelarSolicitante()
    {
        Intent intent = new Intent(this, SeleccionarSolicitantesActitity.class );
        startActivity(intent);
    }

}
