package com.fioritech.car.bussiness.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Vehiculo {
    private String marca;
    private String modelo;
    private int anio;
    private String matricula;
    private String color;
    private int capacidad;
    private String tipo;
    private String transmision;
    private String motor;
    private BigDecimal costoPorDia;
}
