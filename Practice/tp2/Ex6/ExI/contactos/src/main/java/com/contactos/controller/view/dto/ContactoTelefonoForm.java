package com.contactos.controller.view.dto;

import com.contactos.business.domain.enumeration.TipoTelefono;

public class ContactoTelefonoForm {

    private Long id;
    private String numero;
    private TipoTelefono tipo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoTelefono getTipo() {
        return tipo;
    }

    public void setTipo(TipoTelefono tipo) {
        this.tipo = tipo;
    }
}
