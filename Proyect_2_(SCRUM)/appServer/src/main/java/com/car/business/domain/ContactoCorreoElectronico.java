package com.car.business.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "contactos_correo")
public class ContactoCorreoElectronico extends Contacto {

    @Column(nullable = false)
    private String email;

}
