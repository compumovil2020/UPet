package com.example.proyectoupet.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class UserData {

    private String nombre;
    private String apellido;
    private String usuario;
    private String tipoUsuario;
    private String celular;
    private String direccion;
    private String ciudad;
    private String barrio;

    public UserData(String nombre, String apellido, String usuario, String tipoUsuario, String celular, String direccion, String ciudad, String barrio) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.usuario = usuario;
        this.tipoUsuario = tipoUsuario;
        this.celular = celular;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.barrio = barrio;
    }
}
