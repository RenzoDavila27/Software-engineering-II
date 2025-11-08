package com.car.business.domain;

import com.car.business.domain.enums.TipoTelefono;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "contactos_telefonicos")
public class ContactoTelefonico extends Contacto {

    @Column(nullable = false)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_telefono", nullable = false)
    private TipoTelefono tipoTelefono;

}
