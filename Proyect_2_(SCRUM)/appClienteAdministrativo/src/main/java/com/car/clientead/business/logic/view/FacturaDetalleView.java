package com.car.clientead.business.logic.view;

import java.util.List;

import com.car.clientead.client.dto.AlquilerDto;
import com.car.clientead.client.dto.ClienteDto;
import com.car.clientead.client.dto.DetalleFacturaDto;
import com.car.clientead.client.dto.FacturaDto;
import com.car.clientead.client.dto.PromocionDto;
import com.car.clientead.client.dto.VehiculoDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FacturaDetalleView {

    private FacturaDto factura;
    private List<DetalleLinea> detalles;
    private AlquilerDto alquiler;
    private ClienteDto cliente;
    private VehiculoDto vehiculo;

    @Getter
    @AllArgsConstructor
    public static class DetalleLinea {
        private DetalleFacturaDto detalle;
        private PromocionDto promocion;
    }
}
