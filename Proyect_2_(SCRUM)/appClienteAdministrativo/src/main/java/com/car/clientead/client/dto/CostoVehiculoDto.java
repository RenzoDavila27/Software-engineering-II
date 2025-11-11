package com.car.clientead.client.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CostoVehiculoDto extends BaseDto<String> {

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private Double costo;

    @JsonIgnoreProperties(value = "costoVehiculoDto", allowSetters = true)
    private CaracteristicaVehiculoDto caracteristicaVehiculoDto;
}
