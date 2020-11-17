package com.example.proyectoupet.model;

import android.widget.EditText;

import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Paseo implements Serializable {

    String fecha;

    String horaInicio;

    String horaFin;

    int capacidad;

    Double precio;

    List<Parada> paradas;

    public Paseo(String fecha, String horaInicio, String horaFin, int capacidad, Double precio, List<Parada> paradas) {
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.capacidad = capacidad;
        this.precio = precio;
        this.paradas = paradas;
    }
}
