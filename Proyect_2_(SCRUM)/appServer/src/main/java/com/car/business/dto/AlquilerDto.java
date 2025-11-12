package com.car.business.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlquilerDto extends BaseDto<String> {

    private String clienteId;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private String documentacionId;
    private String vehiculoId;
    private String caracteristicaVehiculoId;
}
