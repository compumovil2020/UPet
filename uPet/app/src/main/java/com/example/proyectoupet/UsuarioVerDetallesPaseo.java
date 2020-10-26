package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.proyectoupet.services.CustomSpinnerMascotasAdapter;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

public class UsuarioVerDetallesPaseo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView txtNombrePaseador;
    private TextView txtFecha;
    private TextView txtHora;
    private TextView txtNumMascotas;
    private TextView txtRanking;
    private TextView txtCosto;

    private Button btnRealizarSeguimiento;
    private Button btnAnterior;
    private Button btnSiguiente;
    private Button btnVolver;
    private Spinner spinnerMascotas;

    private String[] nombresPerros={"Becquer","Elias","Lune","Paco","Laika"};
    private int perros[] = {R.drawable.perrito, R.drawable.perro_elias, R.drawable.perro_lune, R.drawable.pug, R.drawable.perfil};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_ver_detalles_paseo);

        txtNombrePaseador = findViewById(R.id.txtNombrePaseador);
        txtFecha = findViewById(R.id.txtFecha);
        txtHora = findViewById(R.id.txtHora);
        txtNumMascotas = findViewById(R.id.txtNumeroMascotas);
        txtRanking = findViewById(R.id.txtRanking);
        txtCosto = findViewById(R.id.txtCosto);

        btnRealizarSeguimiento = findViewById(R.id.btnRealizarSeguimientoDetallesPaseo);
        btnAnterior = findViewById(R.id.btnAnteriorDetallesPaseo);
        btnSiguiente = findViewById(R.id.btnSiguienteDetallesPaseo);
        btnVolver = findViewById(R.id.btnVolverDetallesPaseo);

        String[] nombrePerros = obtenerNombresPerros();
        int perros[] = obtenerPerros();
        spinnerMascotas = findViewById(R.id.spinnerUDetallesPaseo);
        spinnerMascotas.setOnItemSelectedListener(this);
        CustomSpinnerMascotasAdapter customAdapter = new CustomSpinnerMascotasAdapter(getApplicationContext(),perros,nombresPerros);
        spinnerMascotas.setAdapter(customAdapter);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapUDetallesPaseo);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

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