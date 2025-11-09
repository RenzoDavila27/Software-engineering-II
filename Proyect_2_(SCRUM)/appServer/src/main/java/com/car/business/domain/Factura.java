package com.car.business.domain;

import com.car.business.domain.enums.EstadoFactura;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Collection;

@Data
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
public class Factura extends BaseEntity<String> {

    private Long numeroFactura;
    private LocalDate fechaFactura;
    private Double totalPagado;

    private EstadoFactura estado;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<DetalleFactura> detalles;

    public void calcular_total(){
        for(DetalleFactura detalle: detalles){
            totalPagado += detalle.getSubtotal();
        }
    }

    @Override
    public String getId() { return id; }

    @Override
    public void setId(String id) { this.id = id; }

    @Override
    public Boolean getEliminado() { return eliminado; }

    @Override
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }
}
