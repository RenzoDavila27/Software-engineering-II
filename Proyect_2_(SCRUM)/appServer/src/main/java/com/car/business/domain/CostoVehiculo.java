package com.car.business.domain;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
public class CostoVehiculo extends BaseEntity<String> {

    private LocalDate fechaDesde;
    private LocalDate fechaHasta = LocalDate.of(9999, 1, 1);
    private Double costo;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private CaracteristicaVehiculo caracteristicaVehiculo;
    
    @Override
    public String getId() { return id; }

    @Override
    public void setId(String id) { this.id = id; }

    @Override
    public Boolean getEliminado() { return eliminado; }

    @Override
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }
}
