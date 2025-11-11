package com.car.clientead.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    @JsonIgnoreProperties(value = "caracteristicaVehiculoDto", allowSetters = true)
    private CostoVehiculoDto costoVehiculoDto;

    @JsonIgnore
    private String imagenDataUri;
}
