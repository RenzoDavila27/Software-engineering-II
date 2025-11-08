package com.car.business.domain;

import com.car.business.domain.enums.EstadoFactura;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Collection;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Factura extends BaseEntity{

    private Long numeroFactura;
    private LocalDate fechaFactura;
    private Double totalPagado;
    private EstadoFactura estado;

    private Collection<DetalleFactura> detalles;

    public void calcular_total(){
        for(DetalleFactura detalle: detalles){
            totalPagado += detalle.getSubtotal();
        }
    }

}
