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

    Date inicioPaseo;

    Date finPaseo;

    int capacidad;

    BigDecimal tarifa;

    List<Parada> paradas;

    public Paseo(Date inicioPaseo, Date finPaseo, int capacidad, BigDecimal tarifa, List<Parada> paradas) {
        this.inicioPaseo = inicioPaseo;
        this.finPaseo = finPaseo;
        this.capacidad = capacidad;
        this.tarifa = tarifa;
        this.paradas = paradas;
    }
}
