package com.contactos.controller.view.dto;

import com.contactos.business.domain.enumeration.TipoTelefono;

public class EmpresaForm {

    private Long id;
    private String nombre;
    private Long personaId;
    private Long contactoId;
    private TipoContactoEmpresaForm tipoContacto = TipoContactoEmpresaForm.CORREO;
    private String correo;
    private String telefono;
    private TipoTelefono tipoTelefono;
    private String observacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public Long getContactoId() {
        return contactoId;
    }

    public void setContactoId(Long contactoId) {
        this.contactoId = contactoId;
    }

    public TipoContactoEmpresaForm getTipoContacto() {
        return tipoContacto;
    }

    public void setTipoContacto(TipoContactoEmpresaForm tipoContacto) {
        this.tipoContacto = tipoContacto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
