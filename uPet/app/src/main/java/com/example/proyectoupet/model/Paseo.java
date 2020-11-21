package com.example.proyectoupet.model;

import java.io.Serializable;
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

    String userId;

    String fecha;

    String horaInicio;

    String horaFin;

    int capacidad;

    Double precio;

    List<Parada> paradas;

    public Paseo(String userId, String fecha, String horaInicio, String horaFin, int capacidad, Double precio, List<Parada> paradas) {
        this.userId = userId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.capacidad = capacidad;
        this.precio = precio;
        this.paradas = paradas;
    }
}
