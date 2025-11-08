package com.car.business.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class Alquiler extends BaseEntity {

    @ManyToOne(optional = false)
    private Cliente cliente;

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    @ManyToOne(optional = false)
    private Documentacion documentacion;

    @ManyToOne(optional = false)
    private Vehiculo vehiculo;

}
