package com.car.clientead.business.logic.view;

import java.time.LocalDate;

import lombok.Data;

@Data
public class VehiculoAlquilerInfo {

    private String alquilerId;
    private String clienteId;
    private String clienteNombre;
    private String clienteDocumento;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private double montoPagado;
}
