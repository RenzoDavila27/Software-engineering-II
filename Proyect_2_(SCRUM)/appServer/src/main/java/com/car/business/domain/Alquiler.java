package com.car.business.domain;

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
@EqualsAndHashCode(callSuper = true)
@Entity
public class Alquiler extends BaseEntity<String> {

    @ManyToOne(optional = false)
    private Cliente cliente;

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    @ManyToOne(optional = false)
    private Documentacion documentacion;

    @ManyToOne(optional = false)
    private Vehiculo vehiculo;

    @Override
    public String getId() { return id; }

    @Override
    public void setId(String id) { this.id = id; }

    @Override
    public Boolean getEliminado() { return eliminado; }

    @Override
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }
}
