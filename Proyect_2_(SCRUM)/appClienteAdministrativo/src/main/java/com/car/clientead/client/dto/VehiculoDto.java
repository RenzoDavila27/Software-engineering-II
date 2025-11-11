package com.car.clientead.client.dto;

import com.car.clientead.client.dto.enums.EstadoVehiculo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VehiculoDto extends BaseDto<String> {

    private EstadoVehiculo estadoVehiculo;
    private String patente;
    private String caracteristicaVehiculoId;
}
