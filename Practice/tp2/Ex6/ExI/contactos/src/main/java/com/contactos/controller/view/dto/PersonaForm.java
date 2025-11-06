package com.contactos.controller.view.dto;

import java.util.ArrayList;
import java.util.List;

public class PersonaForm {

    private Long id;
    private String nombre;
    private String apellido;
    private Long usuarioId;
    private String cuenta;
    private String clave;
    private List<ContactoCorreoForm> correos = new ArrayList<>();
    private List<ContactoTelefonoForm> telefonos = new ArrayList<>();
    private List<Long> contactosEliminar = new ArrayList<>();
    private List<Long> empresasIds = new ArrayList<>();

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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public List<ContactoCorreoForm> getCorreos() {
        return correos;
    }

    public void setCorreos(List<ContactoCorreoForm> correos) {
        this.correos = correos;
    }

    public List<ContactoTelefonoForm> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<ContactoTelefonoForm> telefonos) {
        this.telefonos = telefonos;
    }

    public List<Long> getContactosEliminar() {
        return contactosEliminar;
    }

    public void setContactosEliminar(List<Long> contactosEliminar) {
        this.contactosEliminar = contactosEliminar;
    }

    public List<Long> getEmpresasIds() {
        return empresasIds;
    }

    public void setEmpresasIds(List<Long> empresasIds) {
        this.empresasIds = empresasIds;
    }

}
