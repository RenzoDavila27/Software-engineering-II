package com.car.business.dto;

import com.car.business.domain.enums.EstadoVehiculo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VehiculoDto extends BaseDto<String> {

    private EstadoVehiculo estadoVehiculo;
    private String patente;
    private CaracteristicaVehiculoDto caracteristicaVehiculo;
    private CostoVehiculoDto costoVehiculo;
    /**
     * Identificador plano de la característica asociado al vehículo. Se expone para
     * clientes que solo manejan el id y no requieren todo el objeto embebido.
     */
    private String caracteristicaVehiculoId;

}
