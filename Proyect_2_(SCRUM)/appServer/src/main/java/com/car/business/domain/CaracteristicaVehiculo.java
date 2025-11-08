package com.car.business.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class CaracteristicaVehiculo extends BaseEntity {

    private String marca;
    private String modelo;
    private Long anio;
    private int cantidadAsientos;
    private int cantidadPuertas;
    private int cantidadTotalVehiculos;
    private int cantidadTotalVehiculosAlquilados;

    @ManyToOne(optional = false)
    private CostoVehiculo costoVehiculo;

}
