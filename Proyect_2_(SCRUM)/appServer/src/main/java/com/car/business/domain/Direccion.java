package com.car.business.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@Setter
@Entity
@Table(name = "direcciones")
public class Direccion extends BaseEntity<String> {

    @Column(nullable = false)
    private String calle;

    @Column(nullable = false)
    private String numeracion;

    @Column
    private String barrio;

    @Column(name = "manzana_piso")
    private String manzanaPiso;

    @Column(name = "casa_departamento")
    private String casaDepartamento;

    @Column
    private String referencia;

    @ManyToOne(optional = false)
    @JoinColumn(name = "localidad_id", nullable = false)
    private Localidad localidad;

    @Override
    public String getId() { return id; }

    @Override
    public void setId(String id) { this.id = id; }

    @Override
    public Boolean getEliminado() { return eliminado; }

    @Override
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }
}
