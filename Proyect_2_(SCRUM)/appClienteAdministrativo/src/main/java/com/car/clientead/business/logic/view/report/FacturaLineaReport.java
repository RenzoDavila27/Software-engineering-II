package com.car.clientead.business.logic.view.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FacturaLineaReport {

    private final String descripcion;
    private final String promocion;
    private final Double subtotal;
}
