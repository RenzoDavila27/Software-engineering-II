package com.car.business.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CaracteristicaVehiculoDto extends BaseDto<String> {

    private String marca;
    private String modelo;
    private Long anio;
    private int cantidadAsientos;
    private int cantidadPuertas;
    private int cantidadTotalVehiculos;
    private int cantidadTotalVehiculosAlquilados;
    private ImagenDto imagenDto;
    private List<CostoVehiculoDto> costosVehiculo;

}
