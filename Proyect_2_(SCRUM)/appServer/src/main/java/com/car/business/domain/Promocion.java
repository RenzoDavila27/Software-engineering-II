package com.car.business.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Promocion extends BaseEntity<String>{

    private double porcentajeDescuento;

    private double codigoDescuento;

    private String descripcionDescuento;

    private LocalDate fechaDesde;

    private LocalDate fechaHasta;

    @Override
    public String getId() { return id; }

    @Override
    public void setId(String id) { this.id = id; }

    @Override
    public Boolean getEliminado() { return eliminado; }

    @Override
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }
    
}
