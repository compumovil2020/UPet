package com.example.proyectoupet.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Mascota implements Serializable {

    private String userId;
    private String nombreMascota;
    private Integer edad;
    private String especie;
    private String raza;

    public Mascota(String userId, String nombreMascota, Integer edad, String especie, String raza) {
        this.userId = userId;
        this.nombreMascota = nombreMascota;
        this.edad = edad;
        this.especie = especie;
        this.raza = raza;
    }
}
