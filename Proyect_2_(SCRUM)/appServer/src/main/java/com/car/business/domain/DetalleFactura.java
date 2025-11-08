package com.car.business.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
public class DetalleFactura extends BaseEntity<String> {

    private int cantidad;
    private double subtotal;

    @ManyToOne(optional = false)
    private Alquiler alquiler;

    @Override
    public String getId() { return id; }

    @Override
    public void setId(String id) { this.id = id; }

    @Override
    public Boolean getEliminado() { return eliminado; }

    @Override
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }
}
