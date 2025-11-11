package com.car.clientead.business.logic.view;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
public class DashboardReportView {

    private LocalDate filtroDesde;
    private LocalDate filtroHasta;
    private List<VehiculoResumenView> vehiculosAlquilados = Collections.emptyList();
    private List<ModeloRecaudacionView> recaudacionPorModelo = Collections.emptyList();
    private double totalGeneralRecaudado;
    private long totalAlquileres;
}
