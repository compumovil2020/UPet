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
public class MascotaPuntoRecogida implements Serializable{

    private List<String> mascotasId;

    private String usuarioId;

    private Parada puntoRecogida;

    private String estado;

    public MascotaPuntoRecogida(List<String> mascotasId, String usuarioId, Parada puntoRecogida, String estado) {
        this.mascotasId = mascotasId;
        this.usuarioId = usuarioId;
        this.puntoRecogida = puntoRecogida;
        this.estado = estado;
    }

}
