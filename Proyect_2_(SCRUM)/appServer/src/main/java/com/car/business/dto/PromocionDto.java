package com.car.business.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PromocionDto extends BaseDto<String> {

    private double porcentajeDescuento;
    private double codigoDescuento;
    private String descripcionDescuento;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
}
