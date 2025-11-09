package com.fioritech.car.model;

import lombok.Data;

import java.util.Date;

@Data
public class Alquiler {
    private Date fechaInicio;
    private Date fechaFin;
    private Vehiculo vehiculo;
    private Usuario usuario;
}
