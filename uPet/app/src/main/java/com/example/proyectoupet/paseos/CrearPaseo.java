package com.example.proyectoupet.paseos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.proyectoupet.R;
import com.example.proyectoupet.model.Parada;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CrearPaseo extends AppCompatActivity implements Validator.ValidationListener {

    static final int REQUEST_POINTS = 234;

    Calendar cr;

    @NotEmpty
    EditText crearPaseoDate;

    @NotEmpty
    EditText crearPaseoHora;

    @NotEmpty
    EditText crearPaseoHoraFin;

    @NotEmpty
    @Pattern(regex = "[0-9]+")
    EditText crearPaseoCapacidad;

    @NotEmpty
    @Pattern(regex = "[0-9]+")
    EditText crearPaseoTarifa;

    Button createButton ;
    ArrayList<Parada> stops;


    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_paseo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.home_schedule_walk);
        setSupportActionBar(toolbar);
        init();
        validator = new Validator(this);
        validator.setValidationListener(this);
        createButton = findViewById(R.id.scp_button4);
        createButton.setVisibility(View.INVISIBLE);
    }

    private void init(){
        cr = Calendar.getInstance();
        crearPaseoDate = findViewById(R.id.crearPaseoDate);
        crearPaseoHora = findViewById(R.id.crearPaseoHora);
        crearPaseoHoraFin = findViewById(R.id.crearPaseoHoraFin);
        crearPaseoCapacidad = findViewById(R.id.crearPaseoCapacidad);
        crearPaseoTarifa = findViewById(R.id.crearPaseoTarifa);
    }

    public void seleccionarFecha(View view) {
        int day = cr.get(Calendar.DAY_OF_MONTH);
        int month = cr.get(Calendar.MONTH);
        int year = cr.get(Calendar.YEAR);
        new DatePickerDialog(CrearPaseo.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int nYear, int nMonth, int nDay) {
                crearPaseoDate.setText(nDay+"/"+nMonth+"/"+nYear);
            }
        }, year,month,day).show();
    }

    public void seleccionarHora(View view){
        int hour = cr.get(Calendar.HOUR_OF_DAY);
        int minute = cr.get(Calendar.MINUTE);
        new TimePickerDialog(CrearPaseo.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                EditText aux = (EditText) view;
                aux.setText(i+":"+i1);
            }
        },hour,minute,true).show();
    }

    public void toDefinirRuta(View view){
         validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        Intent i = new Intent(this,SeleccionRutaPaseo.class);
        if(stops != null){
            i.putParcelableArrayListExtra("stops",this.stops);
        }
        startActivityForResult(i,REQUEST_POINTS);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            // Display error messages
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras = intent.getExtras();
        if(extras != null) {
            stops = extras.getParcelableArrayList("stops");
            stops.forEach(x-> {
                System.out.println("!----- "+x.getLatitude()+"----"+x.getLongitude());
            });
            createButton.setVisibility(View.VISIBLE);
        }
    }

    public void createWalk(View v){

        Toast.makeText(this,R.string.ce_walk_creado,Toast.LENGTH_SHORT);
        finish();
    }
}