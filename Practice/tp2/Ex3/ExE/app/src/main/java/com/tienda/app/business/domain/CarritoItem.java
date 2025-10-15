package com.tienda.app.business.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CarritoItem extends BaseEntity<Long> {

    @ManyToOne(optional = false)
    private Carrito carrito;

    @ManyToOne(optional = false)
    private Articulo articulo;

    private Integer cantidad = 1;

    private Double precioUnitario = 0d;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Boolean isEliminado() {
        return eliminado;
    }

    @Override
    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    public Double getSubtotal() {
        double precio = precioUnitario != null ? precioUnitario : 0d;
        int cantidadActual = cantidad != null ? cantidad : 0;
        return precio * cantidadActual;
    }
}
