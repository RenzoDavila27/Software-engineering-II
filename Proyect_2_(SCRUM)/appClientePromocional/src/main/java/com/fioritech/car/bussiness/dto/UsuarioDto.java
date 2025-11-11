package com.fioritech.car.bussiness.dto;

import lombok.Data;

@Data
public class UsuarioDto {
    private String username;
    private String password;
    private String nombre;
    private String apellido;
    private String email;
}
