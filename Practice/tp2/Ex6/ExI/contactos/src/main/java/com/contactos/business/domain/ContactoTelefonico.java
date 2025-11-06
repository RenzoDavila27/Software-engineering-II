package com.contactos.business.domain;

import com.contactos.business.domain.enumeration.TipoTelefono;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "contactos_telefonicos")
public class ContactoTelefonico extends Contacto {

    @Column(nullable = false)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_telefono", nullable = false)
    private TipoTelefono tipoTelefono;

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public TipoTelefono getTipoTelefono() {
        return tipoTelefono;
    }

    public void setTipoTelefono(TipoTelefono tipoTelefono) {
        this.tipoTelefono = tipoTelefono;
    }
}
