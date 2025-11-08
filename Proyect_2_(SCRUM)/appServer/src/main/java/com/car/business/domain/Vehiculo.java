package com.car.business.domain;

import com.car.business.domain.enums.EstadoVehiculo;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Vehiculo extends BaseEntity{

    private EstadoVehiculo estadoVehiculo;
    private String patente;

    @ManyToOne(optional = false)
    private CaracteristicaVehiculo caracteristicaVehiculo;

}
