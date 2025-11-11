package com.car.business.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CostoVehiculoDto extends BaseDto<String> {

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private Double costo;
    private CaracteristicaVehiculoDto caracteristicaVehiculoDto;
}
