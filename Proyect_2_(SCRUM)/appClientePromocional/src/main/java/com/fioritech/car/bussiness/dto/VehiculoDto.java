package com.fioritech.car.bussiness.dto;

import com.fioritech.car.bussiness.domain.enums.EstadoVehiculo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VehiculoDto extends BaseDto<String> {

    private EstadoVehiculo estadoVehiculo;
    private String patente;
    private CaracteristicaVehiculoDto caracteristicaVehiculo;
    private CostoVehiculoDto costoVehiculo;
}
