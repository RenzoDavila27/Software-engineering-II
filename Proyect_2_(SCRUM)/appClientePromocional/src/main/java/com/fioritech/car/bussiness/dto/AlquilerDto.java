package com.fioritech.car.bussiness.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlquilerDto {
    private Date fechaInicio;
    private Date fechaFin;
    private VehiculoDto vehiculo;
    private UsuarioDto usuario;
}
