package com.example.proyectoupet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class AgendarPaseo extends AppCompatActivity {

    DatePickerDialog dpd;
    Calendar cr ;
    TextView tv ;
    Button btn;
    Button botCrear;
    Button botCancelar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_paseo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Agregar Paseo");
        setSupportActionBar(toolbar);
        cr = Calendar.getInstance();
        btn = findViewById(R.id.button6);
        tv = findViewById(R.id.textView13);
        botCancelar = findViewById(R.id.boton_cancPaseo);
        botCrear = findViewById(R.id.boton_agPaseo);
        setUpCalendar();

        /*botCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), HomePaseador.class));
            }
        });

        botCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), HomePaseador.class));
            }
        });*/
    }

    private void setUpCalendar(){
        int day = cr.get(Calendar.DAY_OF_MONTH);
        int month = cr.get(Calendar.MONTH);
        int year = cr.get(Calendar.YEAR);

        tv.setText(day+"/"+month+"/"+year);
        btn.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int day = cr.get(Calendar.DAY_OF_MONTH);
                int month = cr.get(Calendar.MONTH);
                int year = cr.get(Calendar.YEAR);
                dpd = new DatePickerDialog(AgendarPaseo.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int nYear, int nMonth, int nDay) {
                        tv.setText(nDay+"/"+nMonth+"/"+nYear);
                    }
                }, year,month,day);
                dpd.show();
            }
        });


    }
}