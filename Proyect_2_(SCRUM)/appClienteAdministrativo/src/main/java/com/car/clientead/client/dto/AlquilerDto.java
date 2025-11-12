package com.car.clientead.client.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
public class AlquilerDto extends BaseDto<String> {

    private String clienteId;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private String documentacionId;
    private String vehiculoId;
    private boolean entregado;
    private String caracteristicaVehiculoId;
}
