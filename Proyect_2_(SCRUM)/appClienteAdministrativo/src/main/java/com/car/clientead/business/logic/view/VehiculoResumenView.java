package com.car.clientead.business.logic.view;

import java.util.Collections;
import java.util.List;

import com.car.clientead.client.dto.CaracteristicaVehiculoDto;
import com.car.clientead.client.dto.VehiculoDto;

import lombok.Data;

@Data
public class VehiculoResumenView {

    private VehiculoDto vehiculo;
    private CaracteristicaVehiculoDto caracteristica;
    private List<VehiculoAlquilerInfo> alquileres = Collections.emptyList();
    private double totalGenerado;
}
