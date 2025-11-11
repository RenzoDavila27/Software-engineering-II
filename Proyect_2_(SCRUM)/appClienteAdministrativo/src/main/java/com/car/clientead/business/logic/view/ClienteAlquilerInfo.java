package com.car.clientead.business.logic.view;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ClienteAlquilerInfo {

    private String alquilerId;
    private String vehiculoId;
    private String vehiculoPatente;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private double montoPagado;
}
