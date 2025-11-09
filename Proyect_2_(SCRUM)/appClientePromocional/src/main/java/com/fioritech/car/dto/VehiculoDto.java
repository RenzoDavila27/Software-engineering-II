package com.fioritech.car.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehiculoDto {
    private String id;
    private String marca;
    private String modelo;
    private Long anio;
    private int cantidadAsientos;
    private int cantidadPuertas;
    private double costo;
    private int cantidadTotalVehiculos;
    private int cantidadVehiculosAlquilados;
    private byte[] imagen;
    private String imagenBase64;
}
