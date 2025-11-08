package com.car.business.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class DetalleFactura extends BaseEntity{

    private int cantidad;
    private double subtotal;

    @ManyToOne(optional = false)
    private Alquiler alquiler;

}
