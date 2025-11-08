package com.car.business.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.car.business.domain.enums.TipoDocumento;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import com.car.business.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "personas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Persona extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(nullable = false)
    private String numeroDocumento;

    @ManyToOne(optional = false)
    private Contacto contacto;

    @ManyToOne(optional = false)
    private Direccion direccion;

    @ManyToOne(optional = false)
    private Imagen imagen;

}
