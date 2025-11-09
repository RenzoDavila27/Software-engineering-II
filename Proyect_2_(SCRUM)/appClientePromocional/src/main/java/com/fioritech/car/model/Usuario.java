package com.fioritech.car.model;

import lombok.Data;

@Data
public class Usuario {
    private String username;
    private String password;
    private String nombre;
    private String apellido;
    private String email;
}
