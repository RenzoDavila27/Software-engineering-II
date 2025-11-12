package com.car.clientead.client.dto;

import com.car.clientead.client.dto.enums.EstadoVehiculo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
public class VehiculoDto extends BaseDto<String> {

    private EstadoVehiculo estadoVehiculo;
    private String patente;
    private String caracteristicaVehiculoId;
}
