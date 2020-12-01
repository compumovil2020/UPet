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

    public List<String> getMascotasId() {
        return mascotasId;
    }

    public void setMascotasId(List<String> mascotasId) {
        this.mascotasId = mascotasId;
    }

    public Parada getPuntoRecogida() {
        return puntoRecogida;
    }

    public void setPuntoRecogida(Parada puntoRecogida) {
        this.puntoRecogida = puntoRecogida;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
