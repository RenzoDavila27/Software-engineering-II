package com.fioritech.gimnasio.business.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "direcciones")
public class Direccion extends BaseEntity {

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
}
