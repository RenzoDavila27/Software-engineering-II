package com.car.business.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class CostoVehiculo extends BaseEntity{

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private Double costo;

}
