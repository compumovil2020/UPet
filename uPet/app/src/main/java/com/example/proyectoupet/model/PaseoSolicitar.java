package com.example.proyectoupet.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class PaseoSolicitar {
    private String idPaseo;

    private List<MascotaPuntoRecogida> mascotasPuntoRecogida;

    public PaseoSolicitar(String idPaseo, List<MascotaPuntoRecogida> mascotasPuntoRecogida) {
        this.idPaseo = idPaseo;
        this.mascotasPuntoRecogida = mascotasPuntoRecogida;
    }

    public String getIdPaseo() {
        return idPaseo;
    }

    public void setIdPaseo(String idPaseo) {
        this.idPaseo = idPaseo;
    }

    public List<MascotaPuntoRecogida> getMascotasPuntoRecogida() {
        return mascotasPuntoRecogida;
    }

    public void setMascotasPuntoRecogida(List<MascotaPuntoRecogida> mascotasPuntoRecogida) {
        this.mascotasPuntoRecogida = mascotasPuntoRecogida;
    }
}
