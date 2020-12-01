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
public class PaseoUsuario implements Serializable {

    private String idUsuario;

    private List<String> paseosAgendados;

    public PaseoUsuario(String idUsuario, List<String> paseosAgendados) {
        this.idUsuario = idUsuario;
        this.paseosAgendados = paseosAgendados;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<String> getPaseosAgendados() {
        return paseosAgendados;
    }

    public void setPaseosAgendados(List<String> paseosAgendados) {
        this.paseosAgendados = paseosAgendados;
    }
}
