package com.car.business.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Cliente extends Persona{

    @Column(name = "direccion_estadia", nullable = false)
    private String direccionEstadia;

    @ManyToOne(optional = false)
    @Column(name = "nacionalidad_id", nullable = false)
    private Nacionalidad nacionalidad;

}
