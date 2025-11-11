package com.fioritech.car.bussiness.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AlquilerDto {
    private Date fechaInicio;
    private Date fechaFin;
    private VehiculoDto vehiculo;
    private UsuarioDto usuario;
}
